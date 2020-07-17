package com.robot.host.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.host.base.entry.WeatherInfoEntry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WeatherInfoMapper extends BaseMapper<WeatherInfoEntry> {
}
