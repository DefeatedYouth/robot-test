package com.robot.host.base.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    /**
     * 创建人
     */
    @ApiModelProperty(hidden=true)
    private String createUser;
    /**
     * 更新人
     */
    @ApiModelProperty(hidden=true)
    private String updateUser;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间",dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后更新时间",dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public BaseEntity() {

    }
//    public void initCreateInfo(){
//        //获取用户信息
//        SysUser user = SecurityContext.getUser();
//        if(user!=null){
//            this.setCreateTime(new Date());
//            this.setCreateUser(user.getId());
//        }
//    }
//    public void initUpdateInfo(){
//        SysUser user = SecurityContext.getUser();
//        if(user!=null){
//            this.setUpdateUser(user.getId());
//            this.setUpdateTime(new Date());
//        }
//    }


}
