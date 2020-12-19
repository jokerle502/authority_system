package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表
 */
@TableName("t_user")
@Data
public class User extends BaseEntity implements Serializable {

    public User(String userName, String password, String salt, String nickname, String userImg, String tel, Integer sex, String email, String status) {
        this.userName = userName;
        this.password = password;
        this.salt = salt;
        this.nickname = nickname;
        this.userImg = userImg;
        this.tel = tel;
        this.sex = sex;
        this.email = email;
        this.status = status;
    }

    public User() {
    }

    private static final long serialVersionUID = 1L;

    private String userName; // 用户名
    private String password; // 密码
    private String salt; // 加密用户的盐
    private String nickname; // 昵称
    private String userImg; // 用户头像
    private String tel;  // 手机号码
    private Integer sex; // 性别：1、男；-1、女
    private String email; // 邮箱
    private String status;  // 状态：on、可用；其它、禁用

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                ", tel='" + tel + '\'' +
                ", sex=" + sex +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
