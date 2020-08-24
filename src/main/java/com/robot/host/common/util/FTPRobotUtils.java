package com.robot.host.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @description: 机器人主机的ftp工具类
 */
@Component
@Slf4j
public class FTPRobotUtils {

    @Value("${ftpHost}")
    private String ftpHost;

    @Value("${ftpPort}")
    private Integer ftpPort;

    @Value("${ftpUsername}")
    private String ftpUserName;

    @Value("${ftpPassword}")
    private String ftpPassWord;

    public FTPSClient initFTPClient(){
        FTPSClient ftpClient = new FTPSClient();
        ftpClient.setAuthValue("SSL");
        ftpClient.setControlEncoding("utf-8");
        try {
            log.info("connecting...ftp服务器:{}:{}", this.ftpHost, this.ftpPort);
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.login(ftpUserName, ftpPassWord);
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)){
                log.error("connect failed...ftp服务器:{}:{}", this.ftpUserName, this.ftpPassWord);
                return null;
            }
            ftpClient.execPBSZ(0L);
            ftpClient.execPROT("P");
            ftpClient.pwd();
            log.info("connect successfu...ftp服务器:{}:{}", this.ftpUserName, this.ftpPassWord);
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
        FTPClient ftpClient = initFTPClient();
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
        FTPClient ftpClient = initFTPClient();
        ftpClient.enterLocalPassiveMode();
        try {
            log.debug("开始上传文件");
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            if (!ftpClient.changeWorkingDirectory(pathname)) {
                createDirecrotyAndChange(ftpClient, pathname);
            }
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            flag = true;
            log.debug("上传文件成功");
        } catch (Exception e) {
            flag = false;
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
        return flag;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(FTPClient ftpClient,String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    public boolean createDirecrotyAndChange(FTPClient ftpClient,String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
//        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {

                String subDirectory = new String(remote.substring(start, end).getBytes("utf-8"), "utf-8");
                path = path + "/" + subDirectory;
                if (!existFile(ftpClient,path)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        log.debug("创建目录[" + subDirectory + "]失败");
                        ftpClient.changeWorkingDirectory(subDirectory);
                    }
                } else {
                    ftpClient.changeWorkingDirectory(subDirectory);
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }
}
