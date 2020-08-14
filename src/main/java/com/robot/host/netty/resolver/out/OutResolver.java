package com.robot.host.netty.resolver.out;


import com.robot.host.common.dto.MessageAboutRobotDTO;

public interface OutResolver {

    boolean support(MessageAboutRobotDTO outDTO);

    void resolve(MessageAboutRobotDTO message);

    String operationName();

    String className();
}
