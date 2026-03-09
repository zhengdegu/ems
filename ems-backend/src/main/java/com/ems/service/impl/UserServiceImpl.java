package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.dto.LoginRequest;
import com.ems.dto.RegisterRequest;
import com.ems.entity.User;
import com.ems.mapper.UserMapper;
import com.ems.service.UserService;
import com.ems.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(LoginRequest request) {
        User user = lambdaQuery().eq(User::getUsername, request.getUsername()).one();
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getEnabled() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        user.setPassword(null);
        return result;
    }

    @Override
    public void register(RegisterRequest request) {
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
        user.setRole("user");
        user.setEnabled(1);
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
        long count = lambdaQuery().eq(User::getUsername, user.getUsername()).count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
}
