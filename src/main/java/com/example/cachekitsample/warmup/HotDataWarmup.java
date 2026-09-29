package com.example.cachekitsample.warmup;

import com.example.cachekitsample.user.UserMapper;
import io.github.biglv666.cachekit.annotation.CacheWarmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动预热示例：上下文就绪后在后台线程执行一次，方法体内正常走缓存路径，
 * 回填由既有链路完成（启动日志里可看到"cache-kit 预热完成"）。
 */
@Component
public class HotDataWarmup {

    private static final Logger log = LoggerFactory.getLogger(HotDataWarmup.class);

    private final UserMapper userMapper;

    public HotDataWarmup(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @CacheWarmup(order = 10)
    void loadHotUsers() {
        log.info("预热热点用户……");
        userMapper.selectBatchIds(List.of(1L, 2L, 3L));
    }
}
