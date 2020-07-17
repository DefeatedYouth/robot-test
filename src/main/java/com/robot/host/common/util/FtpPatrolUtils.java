package com.robot.host.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;

/**
 * @description: 机器人主机的ftp工具类
 */
@Component
@Slf4j
public class FtpPatrolUtils {
    //ftp服务器地址
    @Value(value = "${ftpHost}")
    public String ftpHost;

    //ftp服务器端口号默认为21
    @Value(value = "${ftpPort}")
    public Integer ftpPort = 21;
    //ftp登录账号
    @Value(value = "${ftpUsername}")
    public String ftpUsername;
    //ftp登录密码
    @Value(value = "${ftpPassword}")
    public String ftpPassword;

//    public FTPSClient ftpClient = null;

    /**
     * 初始化ftp服务器
     */
    public FTPSClient initFtpClient() {
        FTPSClient ftpClient = new FTPSClient(false);
        ftpClient.setAuthValue("SSL");
        ftpClient.setControlEncoding("utf-8");
        try {
            log.info("connecting...ftp服务器:{}:{}", ftpHost, ftpPort);
            ftpClient.connect(ftpHost, ftpPort); //连接ftp服务器
            //     ftpClient.enterLocalPassiveMode();
            ftpClient.login(ftpUsername, ftpPassword); //登录ftp服务器
//            ftpClient.enterLocalPassiveMode(); // important!
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.error("connect failed...ftp服务器:" + this.ftpUsername + ":" + this.ftpPort);
            }
            ftpClient.execPBSZ(0L);
            ftpClient.execPROT("P");
            ftpClient.pwd();
            log.info("connect successfu...ftp服务器:" + this.ftpUsername + ":" + this.ftpPort);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * 上传文件
     *
     * @param pathname       ftp服务保存地址
     * @param fileName       上传到ftp的文件名
     * @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, String originfilename) {
        boolean flag = false;
        InputStream inputStream = null;
        FTPClient ftpClient = initFtpClient();
        try {
            log.debug("开始上传文件");
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(new File(originfilename));
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            if (!ftpClient.changeWorkingDirectory(pathname)) {
                ftpClient.makeDirectory(pathname);
            }
            log.debug("---------" + ftpClient.getReplyCode());
            log.debug("---------" +    ftpClient.getReplyString());
            log.debug("---------" +    ftpClient.getReplyStrings());
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            log.debug("---------" + ftpClient.getReplyCode());
            log.debug("---------" +    ftpClient.getReplyString());
            log.debug("---------" +    ftpClient.getReplyStrings());
            inputStream.close();
            ftpClient.logout();
            flag = true;
            log.debug("上传文件成功");
        } catch (Exception e) {
            log.debug("上传文件失败");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param pathname    ftp服务保存地址
     * @param fileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = initFtpClient();
        ftpClient.enterLocalPassiveMode();
        try {
            log.debug("开始上传文件");
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            if (!ftpClient.changeWorkingDirectory(pathname)) {
                ftpClient.makeDirectory(pathname);
            }
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            flag = true;
            log.debug("上传文件成功");
        } catch (Exception e) {
            log.debug("上传文件失败");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
