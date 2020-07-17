package com.robot.host.base.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * @author Exrickx
 * 前后端交互数据标准
 */
@Data
public class Result<T> implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 消息
     */
    private String message;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 结果对象
     */
    private T result;


    public Result<T> setData(T t){
        this.setSuccess(true);
        this.setResult(t);
        this.setCode(200);
        return this;
    }

    public Result<T> setSuccessMsg(String msg){
        this.setSuccess(true);
        this.setMessage(msg);
        this.setCode(200);
        this.setResult(null);
        return this;
    }

    public Result<T> setData(T t, String msg){
        this.setSuccess(true);
        this.setResult(t);
        this.setCode(200);
        this.setMessage(msg);
        return this;
    }

    public Result<T> setErrorMsg(String msg){
        this.setSuccess(false);
        this.setMessage(msg);
        this.setCode(500);
        return this;
    }

    public Result<T> setErrorMsg(Integer code, String msg){
        this.setSuccess(false);
        this.setMessage(msg);
        this.setCode(code);
        return this;
    }

}
