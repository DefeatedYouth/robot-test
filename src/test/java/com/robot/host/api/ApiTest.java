package com.robot.host.api;

import cn.hutool.json.JSONUtil;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.entry.WeatherInfoEntry;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.base.service.WeatherInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApiTest {


    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private WeatherInfoService weatherInfoService;

    @Test
    public void selectAllRobot(){
        List<RobotInfoEntity> list = robotInfoService.list();
        System.out.println(JSONUtil.toJsonStr(list.get(0)));
    }

    @Test
    public void selectAllWeather(){
        List<WeatherInfoEntry> list = weatherInfoService.list();
        System.out.println(JSONUtil.toJsonStr(list.get(0)));
    }
}
