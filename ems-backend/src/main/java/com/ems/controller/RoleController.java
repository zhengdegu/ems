package com.ems.controller;

import com.ems.annotation.OpLog;
import com.ems.annotation.RequirePermission;
import com.ems.dto.R;
import com.ems.entity.Role;
import com.ems.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @RequirePermission("role:manage")
    public R<?> list(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(required = false) String keyword) {
        return R.ok(roleService.listPage(page, size, keyword));
    }

    @GetMapping("/{id}")
    @RequirePermission("role:manage")
    public R<?> detail(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return R.fail("角色不存在");
        }
        return R.ok(role);
    }

    @PostMapping
    @RequirePermission("role:manage")
    @OpLog(module = "角色管理", action = "新增")
    public R<?> create(@RequestBody Role role) {
        return R.ok(roleService.createRole(role));
    }

    @PutMapping("/{id}")
    @RequirePermission("role:manage")
    @OpLog(module = "角色管理", action = "编辑")
    public R<?> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return R.ok(roleService.updateRole(role));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("role:manage")
    @OpLog(module = "角色管理", action = "删除")
    public R<?> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @PutMapping("/{id}/toggle")
    @RequirePermission("role:manage")
    @OpLog(module = "角色管理", action = "启用/禁用")
    public R<?> toggleEnabled(@PathVariable Long id) {
        try {
            roleService.toggleEnabled(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
