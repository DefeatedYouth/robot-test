package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.mapper.DeviceInfoMapper;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.vo.PageVO;
import com.robot.host.common.util.EasyExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service("deviceInfoService")
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoEntry> implements DeviceInfoService {


    /**
     * 分页
     * @param pageVO
     * @return
     */
    @Override
    public Page<DeviceInfoEntry> listByPageAndName(PageVO pageVO) {
        Page<DeviceInfoEntry> result = null;
        String name = pageVO.getName();
        Page<DeviceInfoEntry> page = new Page<DeviceInfoEntry>(pageVO.getPageNumber(), pageVO.getPageSize());
        if(name == null || "".equalsIgnoreCase(name)){
            result = this.page(page);
        }else{
            result = this.page(page, new QueryWrapper<DeviceInfoEntry>().lambda().like(DeviceInfoEntry::getDeviceName, name));
        }
        return result;
    }

    @Override
    public Boolean insertByUpload(MultipartFile file) {
        Boolean saveBatch = null;
        try {
            List<DeviceInfoEntry> deviceInfoEntryList = EasyExcelUtil.syncReadModel(file.getInputStream(), DeviceInfoEntry.class, 0, 1);
            saveBatch = this.saveBatch(deviceInfoEntryList);
        } catch (IOException e) {
            saveBatch = false;
            e.printStackTrace();
        }
        return saveBatch;
    }
}
