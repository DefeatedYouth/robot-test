package com.robot.host.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("巡视路线")
public class PatrolRouteDTO {

    private String robotCode;


    private String sceneIds;

}
