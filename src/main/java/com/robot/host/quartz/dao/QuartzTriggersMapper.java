package com.robot.host.quartz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.host.quartz.entry.QuartzTriggers;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuartzTriggersMapper extends BaseMapper<QuartzTriggers> {
}
