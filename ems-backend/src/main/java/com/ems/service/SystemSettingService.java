package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.SystemSetting;

import java.util.List;
import java.util.Map;

public interface SystemSettingService extends IService<SystemSetting> {

    List<SystemSetting> listAll();

    SystemSetting getByKey(String key);

    void saveSetting(String key, String value, String label);

    void batchSave(Map<String, String> settings);

    void deleteSetting(String key);
}
