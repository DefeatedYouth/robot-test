package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.PatrolTaskResultEntity;
import com.robot.host.base.mapper.PatrolTaskResultMapper;
import com.robot.host.base.service.PatrolTaskResultService;
import org.springframework.stereotype.Service;

@Service("patrolTaskResultService")
public class PatrolTaskResultServiceImpl extends ServiceImpl<PatrolTaskResultMapper, PatrolTaskResultEntity> implements PatrolTaskResultService {
}
