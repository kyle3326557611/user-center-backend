package com.example.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 * @author kyle
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code; //返回状态码

    private String message;  //返回信息（越详细越好）

    private T data;  //数据本身

    private String description;

    public BaseResponse(int code, String message, T data,String description) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this(code,"",data,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),errorCode.getMessage(),null,errorCode.getDescription());
    }

    public BaseResponse(int code, String message, String description) {
        this(code,message,null,description);
    }
}

