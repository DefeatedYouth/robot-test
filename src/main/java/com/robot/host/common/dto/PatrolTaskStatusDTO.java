package com.robot.host.common.dto;

import lombok.Data;

@Data
public class PatrolTaskStatusDTO {

    private long taskPatrolledId;

    private String taskCode;

    private String description;
}
