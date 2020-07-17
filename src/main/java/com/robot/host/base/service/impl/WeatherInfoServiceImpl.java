package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.WeatherInfoEntry;
import com.robot.host.base.mapper.WeatherInfoMapper;
import com.robot.host.base.service.WeatherInfoService;
import org.springframework.stereotype.Service;

@Service("weatherInfoService")
public class WeatherInfoServiceImpl extends ServiceImpl<WeatherInfoMapper,WeatherInfoEntry> implements WeatherInfoService {
}
