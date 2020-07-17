package com.robot.host.base.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.mapper.RobotInfoMapper;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.constants.EnumRobotOperationType;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.dto.CoordinateDTO;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("robotInfoService")
@Slf4j
public class RobotInfoServiceImpl extends ServiceImpl<RobotInfoMapper, RobotInfoEntity> implements RobotInfoService {

    //单位长度
    private Integer unitLength = 50;



    /**
     * 修改坐标
     * @param robotCode         机器人编码
     * @param operationType     操作指令
     */
    @Override
    public void updateCoordinate(String robotCode, EnumRobotOperationType operationType) {
        RobotInfoEntity robotInfo = this.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));
        Integer posX = Integer.valueOf(robotInfo.getPosX());
        Integer posy = Integer.valueOf(robotInfo.getPosY());
        switch (operationType){
            case qianjin:
                robotInfo.setPosX(String.valueOf(posX + unitLength));
                break;
            case houtui:
                robotInfo.setPosX(String.valueOf(posX - unitLength));
                break;
            case zuozhuan:
                robotInfo.setPosY(String.valueOf(posy - unitLength));
                break;
            case youzhuan:
                robotInfo.setPosY(String.valueOf(posy + unitLength));
                break;
            case yijianfanhang:
                robotInfo.setPosX("0");
                robotInfo.setPosY("0");
                break;
            default:
//                log.info("操作有误，操作指令:{}",operationType);
                return;
        }
        this.saveOrUpdate(robotInfo);
        //坐标更改，返回实时坐标信息
        MessageAboutRobotDTO sendMsgCoordinateVO = new MessageAboutRobotDTO();
        sendMsgCoordinateVO.setMsgType(EnumSendToRobotMsgType.COORDINATE_DATA);
        CoordinateDTO coordinateDTO = new CoordinateDTO();
        coordinateDTO.setRobotCode(robotCode);
        String jsonStr = JSONUtil.toJsonStr(coordinateDTO);
        sendMsgCoordinateVO.setMsgBody(jsonStr);
        String coordinateMsg = JSONUtil.toJsonStr(sendMsgCoordinateVO);
        MessageUtil.sendMessage(coordinateMsg);
    }

    /**
     * 修改坐标
     * @param robotCode     机器人编码
     * @param posX          坐标x
     * @param posY          坐标y
     */
    @Override
    public void updateCoordinate(String robotCode, String posX, String posY) {
        RobotInfoEntity robotInfo = this.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));
        robotInfo.setPosX(posX);
        robotInfo.setPosY(posY);
        this.saveOrUpdate(robotInfo);
        //坐标更改，返回实时坐标信息
        MessageAboutRobotDTO sendMsgCoordinateVO = new MessageAboutRobotDTO();
        sendMsgCoordinateVO.setMsgType(EnumSendToRobotMsgType.COORDINATE_DATA);
        CoordinateDTO coordinateDTO = new CoordinateDTO();
        coordinateDTO.setRobotCode(robotCode);
        String jsonStr = JSONUtil.toJsonStr(coordinateDTO);
        sendMsgCoordinateVO.setMsgBody(jsonStr);
        String coordinateMsg = JSONUtil.toJsonStr(sendMsgCoordinateVO);
        MessageUtil.sendMessage(coordinateMsg);

    }
}
