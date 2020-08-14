package com.robot.host.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robot.host.base.entry.SysConfig;

import java.util.List;

public interface SysConfigService extends IService<SysConfig> {
    List<SysConfig> sysConfigList();

    void updateSysConfig(SysConfig sysConfig, StringBuilder returnMessage);
}
