package com.robot.host.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robot.host.common.constants.EnumRobotOperationType;
import com.robot.host.base.entry.RobotInfoEntity;

import java.util.List;

public interface RobotInfoService extends IService<RobotInfoEntity> {

    void updateCoordinate(String robotCode, EnumRobotOperationType operationType);

    void updateCoordinate(String robotCode, String posX, String posY);

}
