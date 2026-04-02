package com.example.usercenter.Model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;


@TableName(value = "user")
@Data
public class User {
    /**
     * 用户
     */
    @TableId(type = IdType.AUTO)  //告诉程序这是一个自增主键
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 账号
     */
    private String account;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 表示用户状态，0-正常
     */
    private Integer status;

    /**
     * 账号创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除，01
     */
    @TableLogic  //逻辑删除字段
    private Integer isDelete;

    /*
     * 区分用户和管理员，用户-0，管理员-1
     */
    private Integer role;

    /**
     * 编号
     */
    private String planetCode;
}