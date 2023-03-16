package com.reggie.common;

public class Result {
    private Integer code;//1成功 其他表示失败
    private String msg;//失败信息
    private Object data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Result(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result succeed(Object data){
        return new Result(1,data);
    }
    public static Result Error(String msg){
        return new Result(0,msg);
    }

}
