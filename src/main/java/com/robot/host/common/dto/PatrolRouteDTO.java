package com.robot.host.common.dto;

import com.robot.host.base.entry.PatrolTaskEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("巡视路线")
public class PatrolRouteDTO {

    private String robotCode;


    private String sceneIds;

    private PatrolTaskEntity patrolTaskEntity;//设置文件路径

}
