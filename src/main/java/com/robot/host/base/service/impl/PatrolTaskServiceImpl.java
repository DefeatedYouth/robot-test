package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.base.mapper.PatrolTaskMapper;
import com.robot.host.base.service.PatrolTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("patrolTaskService")
@Slf4j
public class PatrolTaskServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTaskEntity> implements PatrolTaskService {
}
