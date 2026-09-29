package com.example.cachekitsample.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 标准 MP BaseMapper——selectById/selectBatchIds/updateById/insert/deleteById
 * 由 cache-kit 自动接入三级缓存与失效，零注解。
 */
public interface UserMapper extends BaseMapper<User> {
}
