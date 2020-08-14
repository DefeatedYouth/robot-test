package com.robot.host.base.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PageVO {

    private Integer pageNumber;
    private Integer pageSize;

    private String name;

    private String[] startTime;

    private String state;

}
