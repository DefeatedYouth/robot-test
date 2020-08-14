package com.robot.host.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.entry.SysConfig;
import com.robot.host.base.entry.WeatherInfoEntry;
import com.robot.host.base.service.SysConfigService;
import com.robot.host.common.constants.EnumRobotComplusStatusDataType;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.common.util.ResultUtil;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.service.QuartzTriggersService;
import com.robot.host.base.service.RobotCommonService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.base.service.WeatherInfoService;
import com.robot.host.base.vo.PageVO;
import com.robot.host.base.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.testng.collections.Lists;

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

    @Autowired
    private SysConfigService sysConfigService;


    /**
     * 发送异常告警数据
     * @param robotCode
     * @param content
     * @return
     */
    @PostMapping("/abormal/warn/{robotCode}/{content}")
    public Object abormalWarnMessage(@PathVariable("robotCode") String robotCode, @PathVariable("content") String content){
        robotCommonService.sendAbormalWarnMessage(robotCode,content);
        return new Result<>().setSuccessMsg("发送异常告警数据成功");
    }



    /**
     * 状态数据列表
     */
    @GetMapping("/statusData/list")
    public Result<List<EnumRobotComplusStatusDataType>> statusDataList(){
        EnumRobotComplusStatusDataType[] statusDataTypes = robotCommonService.statusDataList();
        return ResultUtil.data(Lists.newArrayList(statusDataTypes),"StatusData: 成功获取状态数据");
    }

    /**
     *状态数据响应
     */
    @PostMapping("/statusData/send/{robotCode}/{fullCode}")
    public Result sendStatusData(@PathVariable("robotCode") String robotCode, @PathVariable("fullCode") String fullCode){
        EnumRobotComplusStatusDataType statusDataType = EnumRobotComplusStatusDataType.getEnum(fullCode);
        MessageUtil.statusDataMessage(robotCode,statusDataType);
        return ResultUtil.success("StatusData: 响应数据发送成功");
    }


    @GetMapping("/sysConfig/list")
    public Result<List<SysConfig>> sysConfigList(){
        List<SysConfig> list = sysConfigService.sysConfigList();
        return ResultUtil.data(list);
    }

    @PutMapping("/sysConfig/update")
    public Result<SysConfig> updateSysConfig(@RequestBody SysConfig sysConfig){
        StringBuilder returnMessage = new StringBuilder();
        sysConfigService.updateSysConfig(sysConfig, returnMessage);
        if(returnMessage.length() > 0){
            return  ResultUtil.error(returnMessage.toString());
        }
        return ResultUtil.success("系统配置信息修改成功");
    }



}
