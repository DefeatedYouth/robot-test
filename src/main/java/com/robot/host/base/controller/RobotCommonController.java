package com.robot.host.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.entry.WeatherInfoEntry;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.service.QuartzTriggersService;
import com.robot.host.base.service.RobotCommonService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.base.service.WeatherInfoService;
import com.robot.host.base.vo.PageVO;
import com.robot.host.base.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class RobotCommonController {

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private WeatherInfoService weatherInfoService;

    @Autowired
    private RobotCommonService robotCommonService;

    @Autowired
    private QuartzTriggersService quartzTriggersService;


    /**
     * 发送异常告警数据
     * @param robotCode
     * @param content
     * @return
     */
    @PostMapping("/abormal/warn")
    public Object abormalWarnMessage(@RequestParam("robotCode") String robotCode, @RequestParam("content") String content){
        robotCommonService.sendAbormalWarnMessage(robotCode,content);
        return new Result<>().setSuccessMsg("发送异常告警数据成功");
    }

    /**
     * 获取机器人列表
     * @return
     */
    @GetMapping("/robot/listAll")
    public Result<List<RobotInfoEntity>> selectRobotList(){
        List<RobotInfoEntity> robotInfoList = robotInfoService.list();
        return new Result<List<RobotInfoEntity>>().setData(robotInfoList,"机器人信息查询成功");
    }


    /**
     * 查询任务列表
     * @return
     */
    @PostMapping("/task/listByPage")
    public Result<List<QuartzTriggers>> selectListByPage(@RequestBody PageVO pageVO){
//        List<QuartzTriggers> schedulerList = quartzTriggersService.list(new QueryWrapper<QuartzTriggers>().lambda().);
        Page<QuartzTriggers> quartzTriggersPage = quartzTriggersService.page(new Page<QuartzTriggers>(pageVO.getPageNumber(), pageVO.getPageSize()));

        return new Result<List<QuartzTriggers>>().setData(quartzTriggersPage.getRecords(),"任务查询成功");
    }


    /**
     * 查询微气象数据
     * @return
     */
    @GetMapping("/weather/listAll")
    public Result<List<WeatherInfoEntry>> selectWeatherList(){
        List<WeatherInfoEntry> weatherList = weatherInfoService.list();
        return new Result<List<WeatherInfoEntry>>().setData(weatherList,"微气象数据查询成功");
    }
}
