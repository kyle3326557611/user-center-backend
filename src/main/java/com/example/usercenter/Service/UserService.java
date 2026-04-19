package com.example.usercenter.Service;

import com.example.usercenter.Model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author kyle
 */

public interface UserService extends IService<User> {



    /**
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 再次确认密码
     * @param planetCode 用户编号
     * @return 新用户 id
     */
    //注册
    long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode);


    /**
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      会话
     * @return 返回脱敏后的用户信息
     */
    //登录
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     *
     * @param originUser 用户账户
     * @return 脱敏后的账户
     *
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销（基于 Redis Token）
     * @param token 前端传来的门票
     * @return 是否注销成功
     */
    boolean userLogout(String token);

}
