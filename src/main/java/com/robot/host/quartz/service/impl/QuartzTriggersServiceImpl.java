package com.robot.host.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.quartz.dao.QuartzTriggersMapper;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.service.QuartzTriggersService;
import org.springframework.stereotype.Service;

@Service("quartzTriggersService")
public class QuartzTriggersServiceImpl extends ServiceImpl<QuartzTriggersMapper, QuartzTriggers> implements QuartzTriggersService {
}
