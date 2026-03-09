package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    Page<OperationLog> listPage(int page, int pageSize, String keyword, String module);

    void record(Long userId, String username, String module, String action, String target, String detail, String ip);
}
