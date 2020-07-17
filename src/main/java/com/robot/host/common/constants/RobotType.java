package com.robot.host.common.constants;

import java.util.Arrays;
import java.util.List;

public class RobotType {
    //操作指令
    public static List operations = Arrays.asList(
            "1",
            "2",
            "3",
            "4",
            "21",
            "22"
    );
    //任务下发
    public static List taskIssues = Arrays.asList(
            "101"
    );
    //任务控制
    public static List taskControls = Arrays.asList(
            "41"
    );
    //模型同步
    public static List modelSyncs = Arrays.asList(
            "61"
    );
    public static List responses = Arrays.asList(
            "251"
    );

}
