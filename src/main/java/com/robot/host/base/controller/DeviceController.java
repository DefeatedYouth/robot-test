package com.robot.host.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.vo.PageVO;
import com.robot.host.base.vo.Result;
import com.robot.host.common.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.testng.collections.Lists;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/message/device")
@Slf4j
public class DeviceController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 分页列表
     * @param pageVO
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Result list(@RequestBody PageVO pageVO){
        log.info("获取设备点位分页列表");
        Page<DeviceInfoEntry> devices = deviceInfoService.listByPageAndName(pageVO);
        return ResultUtil.data(devices);
    }

    /**
     * 添加
     * @param deviceInfoEntry
     * @return
     */
    @PostMapping("/insert")
    @ResponseBody
    public Result<DeviceInfoEntry> insert(@RequestBody DeviceInfoEntry deviceInfoEntry){
        boolean save = deviceInfoService.save(deviceInfoEntry);
        if(save){
            log.info("设备点位信息添加成功");
            return ResultUtil.success("设备点位信息添加成功");
        }
        log.info("设备点位信息添加失败");
        return ResultUtil.error("设备点位信息添加失败");
    }

    /**
     * 修改
     * @param deviceInfoEntry
     * @return
     */
    @PutMapping("/update")
    @ResponseBody
    public Result<DeviceInfoEntry> update(@RequestBody DeviceInfoEntry deviceInfoEntry){
        boolean update = deviceInfoService.saveOrUpdate(deviceInfoEntry);
        if (update) {
            log.info("设备点位信息修改成功");
            return ResultUtil.success("设备点位信息修改成功");
        }
        log.info("设备点位信息修改失败");
        return ResultUtil.error("设备点位信息修改失败");
    }


    /**
     * 删除
     * @param deviceIds
     * @return
     */
    @DeleteMapping("/delete/{deviceIds}")
    @ResponseBody
    public Result<DeviceInfoEntry> delete(@PathVariable("deviceIds")String deviceIds){
        boolean delete = deviceInfoService.removeByIds(Lists.newArrayList(deviceIds.split(",")));
        if (delete) {
            log.info("设备点位信息删除成功");
            return ResultUtil.success("设备点位信息删除成功");
        }
        log.info("设备点位信息删除失败");
        return ResultUtil.error("设备点位信息删除失败");
    }


    @PostMapping("/upload")
    @ResponseBody
    public Result<DeviceInfoEntry> insertByUpload(MultipartFile file){
        Boolean flag = deviceInfoService.insertByUpload(file);
        if(flag){
            log.info("设备点位信息导入成功");
            return ResultUtil.success("设备点位信息导入成功");
        }
        log.info("设备点位信息导入失败");
        return ResultUtil.error("设备点位信息导入失败");

    }

    @GetMapping("/download")
    public void downloadTemplate(HttpServletResponse response){
        OutputStream out = null;
        InputStream is = null;
        String fileName = "设备点位信息模板_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("template/设备点位信息模板.xlsx");
            out = response.getOutputStream();

            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.addHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] bytes = new byte[1024];
            int len = -1;
            while((len = is.read(bytes)) != -1){
                out.write(bytes, 0, len);
            }
            log.info("设备点位模板下载成功");
        } catch (IOException e) {
            log.error("设备点位模板下载失败");
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
