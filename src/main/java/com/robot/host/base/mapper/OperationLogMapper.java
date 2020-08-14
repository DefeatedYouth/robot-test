package com.robot.host.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.host.base.entry.OperationLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogEntity> {
}
