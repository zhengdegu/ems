package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Alarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AlarmMapper extends BaseMapper<Alarm> {

    @Select("SELECT level, COUNT(*) as count FROM alarm WHERE status != '已处理' GROUP BY level")
    List<Map<String, Object>> countByLevel();
}
