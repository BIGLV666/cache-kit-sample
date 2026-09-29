package com.example.cachekitsample.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 示例实体：MyBatis-Plus 注解即 cache-kit 的元数据（@TableName/@TableId），无需任何缓存注解。
 */
@TableName("t_user")
public class User {

    @TableId
    private Long userId;
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
