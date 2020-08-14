package com.robot.host.base.controller;

import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.base.vo.Result;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message/robot")
public class RobotController {

    public static final String ROBOT_PREFIX = "Robot:";

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private OperationLogService operationLogService;


    /**
     * 获取机器人列表
     * @return
     */
    @GetMapping("/listAll")
    public Result<List<RobotInfoEntity>> selectRobotList(){
        List<RobotInfoEntity> robotInfoList = robotInfoService.list();
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取机器人列表",
                null, null, null, this.getClass().getCanonicalName());
        return ResultUtil.data(robotInfoList, ROBOT_PREFIX + "查询成功");
    }

    /**
     * 根据robotInfoId获取机器人数据
     * @return
     */
    @GetMapping("/getOne/{robotInfoId}")
    public Result<RobotInfoEntity> getOne(@PathVariable("robotInfoId")Long robotInfoId){
        RobotInfoEntity robot = robotInfoService.getById(robotInfoId);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取" + robot.getCode() + "详细信息",
                null, null, null, this.getClass().getCanonicalName());
        if (robot == null) {
            return ResultUtil.error(ROBOT_PREFIX + "当前查询数据不存在");
        }
        return ResultUtil.data(robot);
    }

    /**
     * 添加机器人信息
     * @param robotInfoEntity
     * @return
     */
    @PostMapping("/insert")
    public Result<RobotInfoEntity> insert(@RequestBody RobotInfoEntity robotInfoEntity){
        boolean save = robotInfoService.save(robotInfoEntity);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "添加机器人信息：" + robotInfoEntity,
                robotInfoEntity.getRobotInfoId(), null, null, this.getClass().getCanonicalName());
        if(!save){
            return ResultUtil.error(ROBOT_PREFIX + "添加失败");
        }
        return ResultUtil.success(ROBOT_PREFIX + "添加成功");
    }

    /**
     * 修改机器人信息
     * @param robotInfoEntity
     * @return
     */
    @PutMapping("/update")
    public Result<RobotInfoEntity> update(@RequestBody RobotInfoEntity robotInfoEntity){
        boolean update = robotInfoService.updateById(robotInfoEntity);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "修改机器人信息：" + robotInfoEntity,
                robotInfoEntity.getRobotInfoId(), null, null, this.getClass().getCanonicalName());
        if(!update){
            return ResultUtil.error(ROBOT_PREFIX + "修改失败");
        }
        return ResultUtil.success(ROBOT_PREFIX + "修改成功");
    }

    /**
     * 根据robotInfoId删除机器人信息
     * @return
     */
    @DeleteMapping("/delete/{robotInfoId}")
    public Result<RobotInfoEntity> delete(@PathVariable("robotInfoId") Long robotInfoId){
        boolean delete = robotInfoService.removeById(robotInfoId);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "删除机器人信息，id为：" + robotInfoId,
                robotInfoId, null, null, this.getClass().getCanonicalName());
        if(!delete){
            return ResultUtil.error(ROBOT_PREFIX + "删除失败");
        }
        return ResultUtil.success(ROBOT_PREFIX + "删除成功");
    }
}
