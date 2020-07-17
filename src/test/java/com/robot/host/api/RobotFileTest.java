package com.robot.host.api;

import com.robot.host.netty.service.RobotFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RobotFileTest {

    @Autowired
    RobotFileService robotFileService;

    @Test
    public void createFile(){
        System.out.println(robotFileService.uploadRobotFile());
    }
}
