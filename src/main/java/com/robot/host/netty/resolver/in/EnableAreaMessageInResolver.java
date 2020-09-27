package com.robot.host.netty.resolver.in;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.BaseXmlDTO;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.XmlInEnableDTO;
import com.robot.host.common.dto.XmlInRobotMoelSynDTO;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 检修区域下发指令
 * @author xiatian
 * @date 2020/9/21 14:35
 */
public class EnableAreaMessageInResolver implements InResolver {


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private DeviceInfoService deviceInfoService;

    public EnableAreaMessageInResolver(DeviceInfoService deviceInfoService) {
        this.deviceInfoService = deviceInfoService;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        if (!StringUtils.equals(judgeInDTO.getType(), NettyConstants.OUT_CODE_ENABLE + "")) {
            return false;
        }
        if (!StringUtils.equals(judgeInDTO.getCommand(), NettyConstants.OUT_CODE_ENABLE_COMMAND + "")) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        ProtocolMessage protocol = new ProtocolMessage();
        BaseXmlDTO enableDTO = new BaseXmlDTO();

        try {
            //响应信息
            enableDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
            enableDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
            enableDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
            enableDTO.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_ITEMS + "");
            //修改检修区域信息
            XmlInEnableDTO xmlInEnableDTO = XmlBeanUtils.xmlToBean(message.getBody(), XmlInEnableDTO.class);
            XmlInEnableDTO.Item item = xmlInEnableDTO.getItems().get(0);
            List<DeviceInfoEntry> list = deviceInfoService.list(new QueryWrapper<DeviceInfoEntry>().lambda().in(DeviceInfoEntry::getDeviceId, item.getDeviceList().split(",")));
            for (DeviceInfoEntry device : list) {
                device.setEnable(item.getEnable());
                if(item.getEnable() == 1){
                    device.setStartTime(sdf.parse(item.getStartTime()));
                    device.setEndTime(sdf.parse(item.getEndTime()));
                }
            }
            deviceInfoService.saveOrUpdateBatch(list);
            enableDTO.setCode(NettyConstants.RESPONSE_CODE_SUCCESS + "");
        } catch (Exception e) {
            enableDTO.setCode(NettyConstants.RESPONSE_CODE_ERROR + "");
            e.printStackTrace();
        }finally {
            protocol.setBody(XmlBeanUtils.beanToXml(enableDTO, BaseXmlDTO.class));
        }
        return protocol;
    }

    @Override
    public String operationName() {
        return "检修区域";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }
}
