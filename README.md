


# 🧑‍💼 用户管理中心 · User Center Backend

> 本人的第一个完整后端项目 —— 基于 Spring Boot 3 构建的用户注册、分布式登录与权限管理系统。

------

## 📖 项目简介

用户管理中心（User Center）是一个基于 **Spring Boot 3** 的 RESTful 后端服务，提供用户注册、基于 Token 的无状态登录、信息查询等核心功能。项目集成了 MyBatis-Plus 简化数据库操作，**引入 Redis 构建分布式登录状态**，并使用 Knife4j 提供在线 API 文档，方便接口调试与对接。

**本项目严格遵循企业级开发规范，注重代码分层、安全性与高内聚低耦合设计。**

------

## 🛠️ 技术栈

| 技术          | 版本  | 说明                      |
| ------------- | ----- | ------------------------- |
| Java          | 17    | 主要开发语言              |
| Spring Boot   | 3.3.5 | 核心框架                  |
| MyBatis-Plus  | 3.5.5 | ORM 框架，简化数据库 CRUD |
| MySQL         | 8.x   | 关系型数据库              |
| Redis         | 6.x+  | 分布式缓存与登录状态管理  |
| Knife4j       | 4.5.0 | API 文档（OpenAPI 3）     |
| Lombok        | -     | 减少样板代码              |
| Commons-Lang3 | -     | Apache 工具库             |
| Maven         | -     | 项目构建与依赖管理        |

------

## ✨ 主要功能

- ✅ 用户注册（账号 + 密码，含防并发重号校验）
- ✅ 用户登录（基于 Redis + Token 的无状态鉴权）
- ✅ 获取当前登录用户信息（Token 自动续签与脱敏）
- ✅ 用户列表查询（管理员权限）
- ✅ 用户删除（管理员权限）
- ✅ 在线 API 文档自动化生成（Knife4j）

------

## 💡 架构设计与项目亮点 

1. **分布式登录架构演进**：
   - 摒弃传统单机 Session，引入 **Redis 配合 UUID Token** 机制重构用户鉴权模块。实现服务端无状态化认证与 Token 自动续签，彻底解决多台服务器下的登录状态共享痛点，并利用内存级读取大幅降低 MySQL 高频查询压力。
2. **高并发场景下的数据一致性兜底**：
   - 针对并发注册可能引发的“星球编号”重复竞态条件漏洞，在 MySQL 层面引入**唯一索引 (Unique Index)** 进行底层兜底拦截，结合业务层异常捕获，确保核心数据的绝对唯一性。
3. **规范化前后端交互链路**：
   - 独立设计 `GlobalExceptionHandler` 全局统一异常处理器，通过 `@RestControllerAdvice` 实现业务异常的精准拦截。
   - 封装 `BaseResponse` 泛型类与 `ErrorCode` 错误码枚举，统一 API 响应体格式，极大降低前后端联调成本。
4. **严密的数据安全防护**：
   - **存储安全**：摒弃明文存储，采用 `MD5 + 随机盐值 (Salt)` 算法对密码进行单向散列加密，有效抵御彩虹表反向破解。
   - **传输安全**：严格规范 `DTO` (如 `UserRegisterRequest`) 与 `Entity` 的实体分层，实现前端请求数据与底层数据模型的物理隔离；并在接口层重写 `getSafetyUser` 方法，对敏感隐私数据进行强制脱敏。
5. **高内聚的代码组织**：
   - 严格遵循 `Controller -> Service -> Mapper` 标准三层架构，依托 Spring 的 IoC (控制反转) 与 `@Resource` 依赖注入，实现组件间的低耦合。

---

## 🚀 快速启动

### 前置条件

- JDK 17+
- Maven 3.6+
- MySQL 8.x
- **Redis 6.x+ (必须启动本地 Redis 服务)**

### 1. 克隆项目

```bash
git clone https://github.com/kyle3326557611/user-center-backend.git
cd user-center-backend
```

### 2. 初始化数据库

在 MySQL 中创建数据库：

```sql
CREATE DATABASE universe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置应用环境

在 `src/main/resources/application.yml` 中修改数据库与 Redis 的连接信息：

```yaml
spring:
  datasource:
    # 数据库名：universe，已配置时区与防乱码参数
    url: jdbc:mysql://localhost:3306/universe?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 050217
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

或者在 IDEA 中直接运行 `UserCenterApplication.java` 主类。
启动成功后，默认访问地址：`http://localhost:8080`

------

## 📄 API 文档

项目集成了 Knife4j，启动后访问在线文档接口（可直接进行接口调试）：

```text
http://localhost:8080/doc.html
```

------

## 📁 项目结构

```text
user-center-backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/usercenter/
│   │   │   ├── common/         # 核心组件：通用返回体 BaseResponse、错误码、ResultUtils 工具类
│   │   │   ├── exception/      # 异常处理：自定义 BusinessException、全局异常拦截器
│   │   │   ├── contant/        # 全局常量：如用户身份权限标识
│   │   │   ├── Controller/     # 控制层：处理 HTTP 请求与 Token 校验
│   │   │   ├── Service/        # 业务逻辑层：防重校验、密码加盐等核心逻辑
│   │   │   ├── Mapper/         # 数据访问层：MyBatis-Plus 接口
│   │   │   ├── Model/          # 实体模型层：Entity 实体与 DTO 请求/响应对象
│   │   │   └── UserCenterApplication.java
│   │   └── resources/
│   │       └── application.yml # 全局配置文件
│   └── test/                   # 单元测试
├── pom.xml                     # Maven 依赖配置
└── README.md
```

------

## 🙋 作者

- **Kyle** · 第一个后端项目，夯实基础，持续打怪升级中 💪
- GitHub：[@kyle3326557611](https://github.com/kyle3326557611)

------

## 📝 License

本项目仅用于个人学习与实战演练，暂无开源协议。
```
