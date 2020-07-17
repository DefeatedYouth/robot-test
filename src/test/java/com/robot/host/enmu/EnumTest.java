package com.robot.host.enmu;

import com.alibaba.fastjson.JSONObject;
import com.robot.host.common.constants.EnumRobotOperationType;
import org.testng.annotations.Test;

public class EnumTest {

    @Test
    public void operationToString(){
        EnumRobotOperationType anEnum = EnumRobotOperationType.getEnum("2-2");
        System.out.println(JSONObject.toJSON(anEnum));
        System.out.println(anEnum.toString());
    }
}
