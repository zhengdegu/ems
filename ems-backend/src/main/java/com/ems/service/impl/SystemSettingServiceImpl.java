package com.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.SystemSetting;
import com.ems.mapper.SystemSettingMapper;
import com.ems.service.SystemSettingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting> implements SystemSettingService {

    @Override
    public List<SystemSetting> listAll() {
        return list();
    }

    @Override
    public SystemSetting getByKey(String key) {
        return lambdaQuery().eq(SystemSetting::getSettingKey, key).one();
    }

    @Override
    public void saveSetting(String key, String value, String label) {
        SystemSetting setting = getByKey(key);
        if (setting == null) {
            setting = new SystemSetting();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setLabel(label);
            save(setting);
        } else {
            setting.setSettingValue(value);
            if (label != null) {
                setting.setLabel(label);
            }
            updateById(setting);
        }
    }

    @Override
    public void batchSave(Map<String, String> settings) {
        settings.forEach((key, value) -> saveSetting(key, value, null));
    }

    @Override
    public void deleteSetting(String key) {
        lambdaUpdate().eq(SystemSetting::getSettingKey, key).remove();
    }
}
