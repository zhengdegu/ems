package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.SparePart;

public interface SparePartService extends IService<SparePart> {

    Page<SparePart> listPage(int page, int pageSize, String keyword, String type);

    SparePart detail(Long id);

    void createSparePart(SparePart sparePart);

    void updateSparePart(Long id, SparePart sparePart);

    void deleteSparePart(Long id);

    void inbound(Long id, int quantity, String remark);

    void outbound(Long id, int quantity, String remark);
}
