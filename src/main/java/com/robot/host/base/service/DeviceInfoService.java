package com.robot.host.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

public interface DeviceInfoService extends IService<DeviceInfoEntry> {

    Page<DeviceInfoEntry> listByPageAndName(PageVO pageVO);

    Boolean insertByUpload(MultipartFile file);
}
