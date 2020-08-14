package com.robot.host.base.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.service.*;
import com.robot.host.common.constants.EnumRobotComplusStatusDataType;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.XmlOutRobotAbormalWarnDTO;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.netty.resolver.out.CommonOutResolver;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.common.util.XmlBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;

@Service("robotCommonService")
@Slf4j
public class RobotCommonServiceImpl implements RobotCommonService {

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 机器人异常告警数据
     * @param robotCode
     * @param content
     */
    @Override
    public void sendAbormalWarnMessage(String robotCode, String content) {

        String currentDate = CommonOutResolver.sdf.format(System.currentTimeMillis());

        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));

        //封装 XmlDTO
        XmlOutRobotAbormalWarnDTO abormalWarnDTO = new XmlOutRobotAbormalWarnDTO();
        abormalWarnDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        abormalWarnDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        abormalWarnDTO.setType(NettyConstants.IN_CODE_ABORMAL_WARN + "");

        XmlOutRobotAbormalWarnDTO.Item item = new XmlOutRobotAbormalWarnDTO.Item();
        item.setRobotCode(robotInfo.getCode());
        item.setRobotName(robotInfo.getName());
        item.setTime(currentDate);
        item.setContent(content);

        abormalWarnDTO.setItems(Lists.newArrayList(item));

        String jsonStr = XmlBeanUtils.beanToXml(abormalWarnDTO, XmlOutRobotAbormalWarnDTO.class);

        MessageAboutRobotDTO abormalWarnVO = new MessageAboutRobotDTO();
        abormalWarnVO.setMsgType(EnumSendToRobotMsgType.ABNORMAL_WARN);
        abormalWarnVO.setMsgBody(jsonStr);

        String abormalWarnMsg = JSONUtil.toJsonStr(abormalWarnVO);

        MessageUtil.sendMessage(abormalWarnMsg);

    }



    @Override
    public EnumRobotComplusStatusDataType[] statusDataList() {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取状态数据列表",
                null, null, null , this.getClass().getCanonicalName());
        return EnumRobotComplusStatusDataType.values();
    }


}
