package com.robot.host.common.constants;

/**
 * @program: com.face.ele.smart.modules.netty
 * @description:
 * @author: dadi
 * @create: 2020-05-07 16:13
 */
public class MxException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public MxException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public MxException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public MxException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public MxException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public MxException() {
        super();
    }

    public MxException(Throwable throwable) {
        super(throwable);
    }
}
