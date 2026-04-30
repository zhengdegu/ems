package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.annotation.OpLog;
import com.ems.dto.R;
import com.ems.entity.SparePart;
import com.ems.service.SparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/spare-part")
public class SparePartController {

    @Autowired
    private SparePartService sparePartService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String type) {
        Page<SparePart> result = sparePartService.listPage(page, pageSize, keyword, type);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        SparePart sparePart = sparePartService.detail(id);
        if (sparePart == null) {
            return R.fail("备件不存在");
        }
        return R.ok(sparePart);
    }

    @PostMapping
    @OpLog(module = "备件管理", action = "新增")
    public R create(@RequestBody SparePart sparePart) {
        sparePartService.createSparePart(sparePart);
        return R.ok();
    }

    @PutMapping("/{id}")
    @OpLog(module = "备件管理", action = "编辑")
    public R update(@PathVariable Long id, @RequestBody SparePart sparePart) {
        sparePartService.updateSparePart(id, sparePart);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @OpLog(module = "备件管理", action = "删除")
    public R delete(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return R.ok();
    }

    @PostMapping("/{id}/inbound")
    @OpLog(module = "备件管理", action = "入库")
    public R inbound(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            int quantity = (int) body.get("quantity");
            String remark = (String) body.get("remark");
            sparePartService.inbound(id, quantity, remark);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/{id}/outbound")
    @OpLog(module = "备件管理", action = "出库")
    public R outbound(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            int quantity = (int) body.get("quantity");
            String remark = (String) body.get("remark");
            sparePartService.outbound(id, quantity, remark);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
