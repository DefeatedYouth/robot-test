package com.robot.host.poi;

import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.EasyExcelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExcelTest {

    @Autowired
    private RobotInfoService robotInfoService;

    /**
     * E:\Users\wangxinyu\Desktop\机器人模板.xlsx
     */
    @Test
    public void insertRobotInfos() throws IOException {
        FileInputStream fis = new FileInputStream(new File("E:\\Users\\wangxinyu\\Desktop\\机器人模板.xlsx"));
        List<RobotInfoEntity> robotList = EasyExcelUtil.syncReadModel(fis, RobotInfoEntity.class, 0, 1);
        robotList.forEach(System.out::println);
        robotInfoService.saveBatch(robotList);
    }
}
