package com.robot.host.netty.service;

import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSysConfigType;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.RobotSynModelXmlDTO;
import com.robot.host.common.dto.RobotSynRobotModelXmlDTO;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.FTPRobotUtils;
import com.robot.host.common.util.SysConfigUtil;
import com.robot.host.common.util.XmlBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;

import java.io.*;
import java.util.List;

@Service("robotFileService")
@Slf4j
public class RobotFileService {

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private FTPRobotUtils ftpUtils;

    @Autowired
    private OperationLogService operationLogService;


    /**
     * 上传设备文件
     * @return  设备文件路径
     */
    public String uploadDeviceFile(){
        String deviceFile = SysConfigUtil.get(EnumSysConfigType.DeviceFile.getName());
        OutputStream out = null;
        InputStream in = null;
        try {
            List<DeviceInfoEntry> deviceList = deviceInfoService.list();
            RobotSynModelXmlDTO modelVO = new RobotSynModelXmlDTO();
            List<RobotSynModelXmlDTO.Model> models = Lists.newArrayList();
            deviceList.forEach(device -> {
                RobotSynModelXmlDTO.Model model = new RobotSynModelXmlDTO.Model();
                model.setDeviceId(device.getDeviceId());
                model.setDeviceName(device.getDeviceName());
                model.setBayId(device.getBayId());
                model.setBayName(device.getBayName());
                model.setMainDeviceId(device.getMainDeviceId());
                model.setMainDeviceName(device.getMainDeviceName());
                model.setDeviceType(device.getDeviceType() + "");
                model.setMeterType(device.getMeterType() + "");
                model.setAppearanceType(device.getAppearanceType() + "");
                model.setSaveTypeList(device.getSaveTypeList());
                model.setRecognitionTypeList(device.getRecognitionTypeList());
                model.setPhase(device.getPhase() + "");
                model.setDeviceInfo(device.getDeviceInfo());
                models.add(model);
            });
            modelVO.setModelList(models);
            String modelMsg = XmlBeanUtils.beanToXml(modelVO, RobotSynModelXmlDTO.class);
            int lastSlash = StringUtils.lastIndexOf(deviceFile, "/");
            String pathName = StringUtils.substring(deviceFile, 0, lastSlash);
            String fileName = StringUtils.substring(deviceFile, lastSlash + 1, deviceFile.length());
            in = new ByteArrayInputStream(modelMsg.getBytes());
            ftpUtils.uploadFile(pathName,fileName,in);

            operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                    SysLogConstant.SYS_LOCAL_STATUS,
                    "[模型同步]生成设备文件",
                    null,null,null, this.getClass().getCanonicalName());

        } finally {
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return deviceFile;
    }

    /**
     * 上传机器人文件
     * @return
     */
    public String uploadRobotFile(){
        String robotFile = SysConfigUtil.get(EnumSysConfigType.RobotFile.getName());
        OutputStream out = null;
        InputStream in = null;
        try {
            List<RobotInfoEntity> robotList = robotInfoService.list();
            RobotSynRobotModelXmlDTO robotModelVO = new RobotSynRobotModelXmlDTO();
            List<RobotSynRobotModelXmlDTO.Model> models = Lists.newArrayList();
            robotList.forEach(robot -> {
                RobotSynRobotModelXmlDTO.Model model = new RobotSynRobotModelXmlDTO.Model();
                model.setRobotName(robot.getName());
                model.setRobotCode(robot.getCode());
                model.setManufacturer(robot.getManufacturer());
                model.setIstransport(robot.getIstransport() + "");
                model.setType(robot.getType() + "");
                model.setMappath(robot.getMappath());
                model.setRobotInfo(robot.getRobotInfo());
                models.add(model);
            });
            robotModelVO.setModelList(models);
            String robotSynMsg = XmlBeanUtils.beanToXml(robotModelVO, RobotSynRobotModelXmlDTO.class);
            int lastSlash = StringUtils.lastIndexOf(robotFile, "/");
            String pathName = StringUtils.substring(robotFile, 0, lastSlash);
            String fileName = StringUtils.substring(robotFile, lastSlash + 1, robotFile.length());
            in = new ByteArrayInputStream(robotSynMsg.getBytes());
            ftpUtils.uploadFile(pathName, fileName, in);

            operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                    SysLogConstant.SYS_LOCAL_STATUS,
                    "[模型同步]生成设备文件",
                    null,null,null, this.getClass().getCanonicalName());
        }finally {
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return robotFile;
    }
}
