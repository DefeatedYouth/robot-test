package com.robot.host.netty.service;

import com.robot.host.common.dto.RobotSynModelXmlDTO;
import com.robot.host.common.dto.RobotSynRobotModelXmlDTO;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.FtpPatrolUtils;
import com.robot.host.common.util.XmlBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;

import java.io.*;
import java.util.List;

@Service("robotFileService")
@Slf4j
public class RobotFileService {

    /**
     * 文件名     .xml
     */
    private String robotFileName = "robotModel.xml";

    private String deviceFileName = "deviceModel.xml";

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private FtpPatrolUtils ftpUtils;

    @Value("${ftpFlag}")
    private Boolean ftpFlag;

    @Value("${storeBasePath}")
    private String storeBasePath;

    @Value("${ftpPath}")
    private String ftpPath;


    /**
     * 上传设备文件
     * @return  设备文件路径
     */
    public String uploadDeviceFile(){
        String deviceFile = "";
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
            if(!ftpFlag){
                deviceFile = storeBasePath + deviceFileName;
                out = new FileOutputStream(new File(deviceFile));
                out.write(modelMsg.getBytes(),0,modelMsg.getBytes().length);
            }else{
                deviceFile = ftpPath + deviceFileName;
                in = new ByteArrayInputStream(modelMsg.getBytes());
                ftpUtils.uploadFile(ftpPath,deviceFileName,in);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info("文件未找到");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("文件生成失败");
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
        String robotFile = "";
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
            if(!ftpFlag){
                robotFile = storeBasePath + "robotModel.xml";
                out = new FileOutputStream(new File(robotFile));
                out.write(robotSynMsg.getBytes(),0,robotSynMsg.getBytes().length);
            }else{
                robotFile = ftpPath + "robotModel.xml";
                in = new ByteArrayInputStream(robotSynMsg.getBytes());
                ftpUtils.uploadFile(ftpPath,"robotModle.xml",in);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info("文件未找到");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("文件生成失败");
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
