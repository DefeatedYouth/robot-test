package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description:
 * @author: dadi
 * @create: 2020-05-05 13:18
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "sendCode",
        "receiveCode",
        "type",
        "code",
        "command",
        "time"
})
@XmlRootElement(name = "Robot")
//@XmlSeeAlso({
//        RegisterInXmlDTO.class,
//        RobotPushCommonInXmlDTO.class,
//        RobotRunInXmlDTO.class,
//        RobotStatusInXmlDTO.class,
//        RobotSynModelXmlDTO.class,
//        SendInstructionsInXmlDTO.class,
//})
@Data
public  class BaseXmlDTO {
    /**
     * <Robot>
     * <SendCode>xxx</SendCode>
     * <ReceiveCode>xxx</ReceiveCode>
     * <Type>xxx</Type>
     * <Code>xxx</Code>
     * <Command>xxx</Command>
     * <Time>xxx</Time>
     * <Items>
     * <Item/>
     * <Item/>
     * </Items>
     * </Robot>
     */



    @XmlElement(name = "SendCode")
    public String sendCode;
    @XmlElement(name = "ReceiveCode")
    public String receiveCode;
    @XmlElement(name = "Type")
    public String type;
    @XmlElement(name = "Code")
    public String code;
    @XmlElement(name = "Command")
    public String command;
    @XmlElement(name = "Time")
    public String time;

    public BaseXmlDTO() {
    }
}
