package com.robot.host.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.host.base.entry.PatrolTaskExecEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 巡检任务执行情况列表
 * 
 * @author dadi
 * @email 
 * @date 2020-05-24 15:35:05
 */
@Mapper
public interface PatrolTaskExecMapper extends BaseMapper<PatrolTaskExecEntity> {

}
