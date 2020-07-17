package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.mapper.DeviceInfoMapper;
import com.robot.host.base.service.DeviceInfoService;
import org.springframework.stereotype.Service;

@Service("deviceInfoService")
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoEntry> implements DeviceInfoService {
}
