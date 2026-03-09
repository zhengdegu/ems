package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.Role;
import com.ems.mapper.RoleMapper;
import com.ems.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public IPage<Role> listPage(int page, int size, String keyword) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Role::getName, keyword).or().like(Role::getCode, keyword);
        }
        wrapper.orderByDesc(Role::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Role createRole(Role role) {
        if (role.getEnabled() == null) {
            role.setEnabled(1);
        }
        save(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        updateById(role);
        return getById(role.getId());
    }

    @Override
    public void deleteRole(Long id) {
        removeById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        Role role = getById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setEnabled(role.getEnabled() == 1 ? 0 : 1);
        updateById(role);
    }
}
