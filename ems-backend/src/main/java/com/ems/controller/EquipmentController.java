package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.dto.R;
import com.ems.entity.Equipment;
import com.ems.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String status,
                  @RequestParam(required = false) String type) {
        Page<Equipment> result = equipmentService.listPage(page, pageSize, keyword, status, type);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        Equipment equipment = equipmentService.detail(id);
        if (equipment == null) {
            return R.fail("设备不存在");
        }
        return R.ok(equipment);
    }

    @PostMapping
    public R create(@RequestBody Equipment equipment) {
        equipmentService.createEquipment(equipment);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody Equipment equipment) {
        equipmentService.updateEquipment(id, equipment);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return R.ok();
    }
}
