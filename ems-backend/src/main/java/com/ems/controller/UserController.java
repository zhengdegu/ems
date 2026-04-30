package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.annotation.OpLog;
import com.ems.annotation.RequirePermission;
import com.ems.dto.R;
import com.ems.entity.User;
import com.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @RequirePermission("user:manage")
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String role,
                  @RequestParam(required = false) Integer enabled) {
        Page<User> result = userService.listPage(page, pageSize, keyword, role, enabled);
        return R.ok(result);
    }

    @PostMapping
    @RequirePermission("user:manage")
    @OpLog(module = "用户管理", action = "新增")
    public R create(@RequestBody User user) {
        try {
            userService.createUser(user);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @RequirePermission("user:manage")
    @OpLog(module = "用户管理", action = "编辑")
    public R update(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("user:manage")
    @OpLog(module = "用户管理", action = "删除")
    public R delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @PutMapping("/{id}/toggle")
    @RequirePermission("user:manage")
    @OpLog(module = "用户管理", action = "启用/禁用")
    public R toggleEnabled(@PathVariable Long id) {
        try {
            userService.toggleEnabled(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/reset-password")
    @RequirePermission("user:manage")
    @OpLog(module = "用户管理", action = "重置密码")
    public R resetPassword(@PathVariable Long id) {
        try {
            userService.resetPassword(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/profile")
    public R updateProfile(@PathVariable Long id, @RequestBody User user) {
        userService.updateProfile(id, user);
        return R.ok();
    }

    @PutMapping("/{id}/password")
    public R changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            userService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
