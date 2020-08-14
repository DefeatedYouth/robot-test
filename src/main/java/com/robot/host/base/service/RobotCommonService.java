package com.robot.host.base.service;

import com.robot.host.base.vo.Result;
import com.robot.host.base.vo.SchedulerJobVO;
import com.robot.host.common.constants.EnumRobotComplusStatusDataType;

public interface RobotCommonService {

    void sendAbormalWarnMessage(String robotCode, String content);

    EnumRobotComplusStatusDataType[] statusDataList();
}
