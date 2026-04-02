package com.example.usercenter.Model.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author kyle
 */
@Schema(description = "用户注册请求体")
@Data
public class UserRegisterRequest implements Serializable {
    @Serial  //防止变量名出错
    private static final long serialVersionUID = 312435345345L;

    private String userAccount;
    private String checkPassword;
    private String userPassword;
    private String planetCode;
}
