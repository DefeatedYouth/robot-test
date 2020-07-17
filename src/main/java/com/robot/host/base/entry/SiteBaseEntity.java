package com.robot.host.base.entry;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SiteBaseEntity extends BaseEntity implements Serializable {
    /**
     * 部门id
     */

    @ApiModelProperty(value = "变电站id", dataType = "Long")
    private Long siteId;

    public SiteBaseEntity() {
    }

//    @Override
//    public void initCreateInfo() {
//        //获取用户信息
//        SysUser user = SecurityContext.getUser();
//        if (user != null) {
//            this.setCreateTime(new Date());
//            this.setCreateUser(user.getId());
//            this.setSiteId(user.getSiteId());
//        }
//    }
}
