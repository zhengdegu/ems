package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.Equipment;
import com.ems.mapper.EquipmentMapper;
import com.ems.service.EquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Override
    public Page<Equipment> listPage(int page, int pageSize, String keyword, String status, String type) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Equipment::getName, keyword)
                    .or().like(Equipment::getCode, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Equipment::getStatus, status);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Equipment::getType, type);
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public Equipment detail(Long id) {
        return getById(id);
    }

    @Override
    public void createEquipment(Equipment equipment) {
        save(equipment);
    }

    @Override
    public void updateEquipment(Long id, Equipment equipment) {
        equipment.setId(id);
        updateById(equipment);
    }

    @Override
    public void deleteEquipment(Long id) {
        removeById(id);
    }
}
