package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.Equipment;

public interface EquipmentService extends IService<Equipment> {

    Page<Equipment> listPage(int page, int pageSize, String keyword, String status, String type);

    Equipment detail(Long id);

    void createEquipment(Equipment equipment);

    void updateEquipment(Long id, Equipment equipment);

    void deleteEquipment(Long id);
}
