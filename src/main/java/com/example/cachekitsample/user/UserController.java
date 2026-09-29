package com.example.cachekitsample.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 演示入口（无需任何缓存注解）：
 * - 读：selectById / selectBatchIds 自动走 Caffeine → Redis → DB
 * - 写：updateById / saveBatch 自动失效（DEL L2 → 清 L1 → 广播 → 延迟双删）
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userMapper.selectById(id);
    }

    @GetMapping("/batch")
    public List<User> batch(@RequestParam List<Long> ids) {
        return userMapper.selectBatchIds(ids);
    }

    @PutMapping("/{id}")
    public User rename(@PathVariable Long id, @RequestBody RenameRequest request) {
        User user = new User();
        user.setUserId(id);
        user.setUserName(request.name());
        userMapper.updateById(user);
        return userMapper.selectById(id);
    }

    /** 演示 IService.updateBatchById（SqlSession 批量通道）也自动失效 */
    @PutMapping("/batch")
    public boolean renameBatch(@RequestBody List<RenameItem> items) {
        List<User> users = items.stream().map(item -> {
            User u = new User();
            u.setUserId(item.id());
            u.setUserName(item.name());
            return u;
        }).toList();
        return userService.updateBatchById(users);
    }

    public record RenameRequest(String name) {
    }

    public record RenameItem(Long id, String name) {
    }
}
