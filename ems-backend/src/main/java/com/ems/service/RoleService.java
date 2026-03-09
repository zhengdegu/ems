package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.Role;

public interface RoleService extends IService<Role> {
    IPage<Role> listPage(int page, int size, String keyword);
    Role createRole(Role role);
    Role updateRole(Role role);
    void deleteRole(Long id);
    void toggleEnabled(Long id);
}
