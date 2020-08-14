package com.robot.host.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.WeatherInfoEntry;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.WeatherInfoService;
import com.robot.host.base.vo.Result;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message/weather")
public class WeatherController {

    public static final String WEATHER_PREFIX = "Weather:";


    @Autowired
    private WeatherInfoService weatherInfoService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 查询微气象数据
     * @return
     */
    @GetMapping("/listAll")
    public Result<List<WeatherInfoEntry>> listAll(){
        List<WeatherInfoEntry> weatherList = weatherInfoService.list();
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取微气象列表",
                null, null, null, this.getClass().getCanonicalName());
        return ResultUtil.data(weatherList, WEATHER_PREFIX + "查询成功");
    }

    /**
     * 通过weatherInfoId获取微气象信息
     * @param robotCode
     * @return
     */
    @GetMapping("/getOne/{weatherInfoId}")
    public Result<WeatherInfoEntry> getOne(@PathVariable("weatherInfoId") Long weatherInfoId){
        WeatherInfoEntry weather = weatherInfoService.getById(weatherInfoId);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取" + weather.getRobotCode() + "相关微气象详细信息",
                null, null, null, this.getClass().getCanonicalName());
        if(weather == null){
            return ResultUtil.error(WEATHER_PREFIX + "当前查询数据不存在");
        }
        return ResultUtil.data(weather);
    }


    /**
     * 添加微气象信息
     * @param weatherInfoEntry
     * @return
     */
    @PostMapping("/insert")
    public Result<WeatherInfoEntry> insert(@RequestBody WeatherInfoEntry weatherInfoEntry){
        boolean save = weatherInfoService.save(weatherInfoEntry);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "添加微气象信息：" + weatherInfoEntry,
                null, null, null, this.getClass().getCanonicalName());
        if(!save){
            return ResultUtil.error(WEATHER_PREFIX + "添加失败");
        }
        return ResultUtil.success(WEATHER_PREFIX + "添加成功");
    }

    /**
     * 根据id修改微气象信息
     * @param weatherInfoEntry
     * @return
     */
    @PutMapping("/update")
    public Result<WeatherInfoEntry> update(@RequestBody WeatherInfoEntry weatherInfoEntry){
        boolean update = weatherInfoService.updateById(weatherInfoEntry);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "修改微气象信息：" + weatherInfoEntry,
                null, null, null, this.getClass().getCanonicalName());
        if (!update){
            return ResultUtil.error(WEATHER_PREFIX + "修改失败");
        }
        return ResultUtil.success(WEATHER_PREFIX + "修改成功");
    }


    @DeleteMapping("/delete/{weatherInfoId}")
    public Result<WeatherInfoEntry> delete(@PathVariable("weatherInfoId") Long weatherInfoId){
        boolean delete = weatherInfoService.removeById(weatherInfoId);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "删除微气象信息，id为：" + weatherInfoId,
                null, null, null, this.getClass().getCanonicalName());
        if(!delete){
            return ResultUtil.error(WEATHER_PREFIX + "删除失败");
        }
        return ResultUtil.success(WEATHER_PREFIX + "删除成功");
    }
}
