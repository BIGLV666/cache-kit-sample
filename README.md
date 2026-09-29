# cache-kit-sample

[cache-kit-spring-boot-starter](https://github.com/BIGLV666/cache-kit-spring-boot-starter) 的最小接入示例：
一个实体（`@TableName` + `@TableId`），零缓存注解，开箱即得——

- **三级缓存**：Caffeine（L1）→ Redis（L2）→ DB，读自动回填
- **写即失效**：updateById / saveBatch 自动 DEL + 广播 + 延迟双删
- **binlog 直写失效**：绕过应用直接改库（DBA/其他服务），秒级感知并失效
- **启动预热**：`@CacheWarmup` 注解方法就绪后自动回填热点数据
- **指标**：`cache-kit.*` Micrometer 计数器（actuator 下可查）

## 前置要求

- JDK 17+、Maven、Docker（或自行准备 Redis 7 与开启 binlog 的 MySQL 8）

## 三步跑起来

```bash
# 1) 起 Redis + MySQL（binlog/ROW，自动建表并写入 3 条种子用户）
docker compose up -d

# 2) 启动应用（8081 端口）
mvn spring-boot:run

# 3) 验证
curl http://localhost:8081/user/1     # {"userId":1,"userName":"alice"}
```

默认用 3308/6380 映射（避免与本地已有容器冲突），如你的 3307/6379 空闲，
改 `docker-compose.yml` 的端口映射和 `application.yml` 的连接配置即可。

## 效果验证

### 1. 三级缓存命中（读不查库）

```bash
curl http://localhost:8081/user/1
curl http://localhost:8081/user/1     # 第二次起走缓存，不查 DB
# 观察 L1 命中计数增长：
curl 'http://localhost:8081/actuator/metrics/cache-kit.l1.requests?tag=result:hit'
```

启动日志还能看到 `cache-kit 预热完成：1 个方法`——`@CacheWarmup` 已把 1/2/3 号用户回填。

### 2. 应用内写失效（改完立即可见）

```bash
curl -X PUT http://localhost:8081/user/1 -H 'Content-Type: application/json' -d '{"name":"alice-v2"}'
# {"userId":1,"userName":"alice-v2"} —— 写后读的已经是新值
```

### 3. binlog 直写失效（绕过应用改库也能感知）

```bash
# 直接把 MySQL 里 user_name 改掉（不经应用、无广播）
docker compose exec mysql mysql -uroot -proot demo -e "UPDATE t_user SET user_name='alice-db-side' WHERE user_id=1"

# 几秒内再读（binlog 行事件触发失效），看到新值：
curl http://localhost:8081/user/1     # {"userId":1,"userName":"alice-db-side"}
```

### 4. IService 批量写失效（0.3.0+，SqlSession 批量通道也覆盖）

```bash
curl -X PUT http://localhost:8081/user/batch -H 'Content-Type: application/json' \
  -d '[{"id":2,"name":"bob-v2"},{"id":3,"name":"carol-v2"}]'
curl 'http://localhost:8081/user/batch?ids=2&ids=3'
```

### 5. 指标一览

```bash
curl 'http://localhost:8081/actuator/metrics' | grep cache-kit
curl 'http://localhost:8081/actuator/metrics/cache-kit.db.loads'     # DB 回源计数
```

## 配置要点（application.yml）

```yaml
cache-kit:
  l1:
    ttl: 30s
  l2:
    ttl: 10m
  binlog:
    enabled: true        # 直写失效开关（需 mysql-binlog-connector-java 依赖）
    server-id: 9001      # 同一 MySQL 上必须唯一
```

## 接入你自己的项目

1. 依赖：`io.github.biglv666:cache-kit-spring-boot-starter:0.3.1`（本示例 pom 即完整最小集）
2. MP 实体保持 `@TableName` / `@TableId`，非 MP 项目用 `@CacheEntity` / `@CacheId` + `@CachedQuery`
3. 完整配置与机制文档见 [cache-kit 主页](https://github.com/BIGLV666/cache-kit-spring-boot-starter#readme)
