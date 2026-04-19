package com.example.usercenter.Model.request;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author kyle
 */

@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 813456789123456L;  //防御性代码规范

    private String userAccount;
    private String userPassword;

}
