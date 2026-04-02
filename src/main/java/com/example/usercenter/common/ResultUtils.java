package com.example.usercenter.common;

/**
 * 返回工具类
 * @author kyle
 */
public class ResultUtils {

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,"ok",data,"");
    }

    /**
     *失败
     */
    public static BaseResponse error(ErrorCode errorCode){
        return  new BaseResponse<>(errorCode);
    }

    /**
     *失败(有自定义描述)
     */
    public static BaseResponse error(ErrorCode errorCode,String message,String description){
        return  new BaseResponse(errorCode.getCode(),message,null,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return  new BaseResponse(errorCode.getCode(),errorCode.getMessage(),description);
    }


    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code,message,null,description);
    }
}
