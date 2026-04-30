package com.ems.aspect;

import com.ems.annotation.RequirePermission;
import com.ems.entity.Role;
import com.ems.entity.User;
import com.ems.mapper.UserMapper;
import com.ems.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String requiredPerm = requirePermission.value();

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new PermissionDeniedException("未登录");
        }

        String username = auth.getPrincipal().toString();
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new PermissionDeniedException("用户不存在");
        }

        // admin 角色直接放行
        if ("admin".equals(user.getRole())) {
            return joinPoint.proceed();
        }

        // 查询角色权限
        Role role = roleService.getOne(
                new LambdaQueryWrapper<Role>().eq(Role::getCode, user.getRole()));
        if (role == null || role.getPermissions() == null) {
            throw new PermissionDeniedException("权限不足");
        }

        // permissions 存储为 JSON 数组字符串，如 ["equipment:view","equipment:edit"]
        String permissions = role.getPermissions();
        if (!permissions.contains(requiredPerm)) {
            throw new PermissionDeniedException("权限不足");
        }

        return joinPoint.proceed();
    }

    public static class PermissionDeniedException extends RuntimeException {
        public PermissionDeniedException(String message) {
            super(message);
        }
    }
}
