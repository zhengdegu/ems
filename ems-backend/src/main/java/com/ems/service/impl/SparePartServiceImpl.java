package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.SparePart;
import com.ems.entity.SparePartRecord;
import com.ems.mapper.SparePartMapper;
import com.ems.mapper.SparePartRecordMapper;
import com.ems.service.SparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SparePartServiceImpl extends ServiceImpl<SparePartMapper, SparePart> implements SparePartService {

    @Autowired
    private SparePartRecordMapper sparePartRecordMapper;

    @Override
    public Page<SparePart> listPage(int page, int pageSize, String keyword, String type) {
        LambdaQueryWrapper<SparePart> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SparePart::getName, keyword)
                    .or().like(SparePart::getCode, keyword));
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(SparePart::getType, type);
        }
        wrapper.orderByDesc(SparePart::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public SparePart detail(Long id) {
        return getById(id);
    }

    @Override
    public void createSparePart(SparePart sparePart) {
        save(sparePart);
    }

    @Override
    public void updateSparePart(Long id, SparePart sparePart) {
        sparePart.setId(id);
        updateById(sparePart);
    }

    @Override
    public void deleteSparePart(Long id) {
        removeById(id);
    }

    @Override
    @Transactional
    public void inbound(Long id, int quantity, String remark) {
        SparePart part = getById(id);
        if (part == null) {
            throw new RuntimeException("备件不存在");
        }
        part.setStock(part.getStock() + quantity);
        updateById(part);

        SparePartRecord record = new SparePartRecord();
        record.setSparePartId(id);
        record.setType("inbound");
        record.setQuantity(quantity);
        record.setRemark(remark);
        sparePartRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void outbound(Long id, int quantity, String remark) {
        SparePart part = getById(id);
        if (part == null) {
            throw new RuntimeException("备件不存在");
        }
        if (part.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        part.setStock(part.getStock() - quantity);
        updateById(part);

        SparePartRecord record = new SparePartRecord();
        record.setSparePartId(id);
        record.setType("outbound");
        record.setQuantity(quantity);
        record.setRemark(remark);
        sparePartRecordMapper.insert(record);
    }
}
