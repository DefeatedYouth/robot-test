package com.robot.host.netty;

import com.robot.host.base.service.RobotCommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageTest {

    @Autowired
    private RobotCommonService robotCommonService;


    @Test
    public void sendAbormalWarnMsg(){
        robotCommonService.sendAbormalWarnMessage("robot1","电池电量低");
    }
}
