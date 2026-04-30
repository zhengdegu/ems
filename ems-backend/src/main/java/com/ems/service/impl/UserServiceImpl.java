package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.dto.LoginRequest;
import com.ems.dto.RegisterRequest;
import com.ems.entity.User;
import com.ems.mapper.UserMapper;
import com.ems.service.LoginLogService;
import com.ems.service.UserService;
import com.ems.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginLogService loginLogService;

    private static final int MAX_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 15;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    @Override
    public Map<String, Object> login(LoginRequest request) {
        String ip = getClientIp();
        String userAgent = getUserAgent();

        User user = lambdaQuery().eq(User::getUsername, request.getUsername()).one();
        if (user == null) {
            loginLogService.recordLogin(null, request.getUsername(), ip, userAgent, "fail", "用户不存在");
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查账号是否被锁定
        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            loginLogService.recordLogin(user.getId(), user.getUsername(), ip, userAgent, "fail", "账号已锁定");
            throw new RuntimeException("账号已锁定，请" + LOCK_MINUTES + "分钟后再试");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 登录失败，增加失败计数
            int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            User update = new User();
            update.setId(user.getId());
            update.setLoginFailCount(failCount);
            if (failCount >= MAX_FAIL_COUNT) {
                update.setLockTime(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                updateById(update);
                loginLogService.recordLogin(user.getId(), user.getUsername(), ip, userAgent, "fail", "密码错误，账号已锁定");
                throw new RuntimeException("密码错误次数过多，账号已锁定" + LOCK_MINUTES + "分钟");
            }
            updateById(update);
            loginLogService.recordLogin(user.getId(), user.getUsername(), ip, userAgent, "fail", "密码错误");
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getEnabled() != 1) {
            loginLogService.recordLogin(user.getId(), user.getUsername(), ip, userAgent, "fail", "账号已禁用");
            throw new RuntimeException("账号已被禁用");
        }

        // 登录成功，重置失败计数
        User update = new User();
        update.setId(user.getId());
        update.setLoginFailCount(0);
        update.setLockTime(null);
        updateById(update);

        loginLogService.recordLogin(user.getId(), user.getUsername(), ip, userAgent, "success", "登录成功");

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        user.setPassword(null);
        result.put("user", user);
        return result;
    }

    @Override
    public void register(RegisterRequest request) {
        // 密码强度校验
        validatePassword(request.getPassword());

        long count = lambdaQuery().eq(User::getUsername, request.getUsername()).count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setDepartment(request.getDepartment());
        user.setEmployeeNo(request.getEmployeeNo());
        user.setRole("user");
        user.setEnabled(1);
        user.setLoginFailCount(0);
        save(user);
    }

    @Override
    public Page<User> listPage(int page, int pageSize, String keyword, String role, Integer enabled) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getName, keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (enabled != null) {
            wrapper.eq(User::getEnabled, enabled);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = page(new Page<>(page, pageSize), wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public void createUser(User user) {
        // 密码强度校验
        validatePassword(user.getPassword());

        long count = lambdaQuery().eq(User::getUsername, user.getUsername()).count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLoginFailCount(0);
        save(user);
    }

    @Override
    public void updateUser(Long id, User user) {
        user.setId(id);
        user.setPassword(null); // 不允许通过此接口改密码
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        removeById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setEnabled(user.getEnabled() == 1 ? 0 : 1);
        updateById(user);
    }

    @Override
    public void resetPassword(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
    }

    @Override
    public void updateProfile(Long id, User user) {
        user.setId(id);
        user.setPassword(null);
        user.setRole(null); // 不允许自己改角色
        user.setEnabled(null);
        updateById(user);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        // 密码强度校验
        validatePassword(newPassword);

        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new RuntimeException("密码至少8位，且包含大小写字母和数字");
        }
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest().getHeader("User-Agent");
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
