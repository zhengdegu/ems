package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    @Select("SELECT type, COUNT(*) as count FROM equipment GROUP BY type")
    List<Map<String, Object>> countByType();

    @Select("SELECT status, COUNT(*) as count FROM equipment GROUP BY status")
    List<Map<String, Object>> countByStatus();

    @Select("SELECT location, COUNT(*) as count FROM equipment GROUP BY location")
    List<Map<String, Object>> countByLocation();
}
