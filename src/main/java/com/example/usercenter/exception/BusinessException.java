package com.example.usercenter.exception;

import com.example.usercenter.common.ErrorCode;
import lombok.Getter;

/**
 * 自定义异常
 * @author kyle
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final String description;


    public BusinessException(String message, int code, String description) {
        super(message);  //runtime异常本身带有的信息
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());  //runtime异常本身带有的信息
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());  //runtime异常本身带有的信息
        this.code = errorCode.getCode();
        this.description = description;
    }

}
