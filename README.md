# 刷题系统 (YueTiku)

一个基于Spring Boot的在线刷题系统，支持多用户、多分类的题目管理和答题功能。

## 项目简介

YueTiku是一个功能完整的在线刷题平台，提供题目管理、用户答题、统计分析等核心功能。系统采用微服务架构设计，支持多用户独立管理自己的题目库和答题记录。核心亮点是加入了AI自动识别题目并导入，供用户方便拥有一个自己的题库小系统。

## 技术栈

- **后端框架**: Spring Boot 2.7.0
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis Plus
- **构建工具**: Maven
- **Java版本**: JDK 8+
- **安全认证**: JWT + Spring Security

## 项目结构

```
YueTiku/
├── YueTiku-admin/          # 管理模块（控制器层）
│   └── src/main/java/com/yuetiku/controller/
│       ├── CategoryController.java
│       ├── QuestionController.java
│       ├── UserController.java
│       └── StatisticsController.java
├── YueTiku-Tiku/           # 题库模块（核心业务）
│   └── src/main/java/com/yuetiku/
│       ├── entity/         # 实体类
│       ├── dto/           # 数据传输对象
│       ├── mapper/        # 数据访问层
│       └── service/       # 业务逻辑层
├── YueTiku-User/           # 用户模块
│   └── src/main/java/com/yuetiku/
│       ├── entity/         # 用户实体
│       ├── dto/           # 用户相关DTO
│       ├── service/       # 用户服务
│       └── util/          # 工具类
├── YueTiku-Answer/         # 答题模块
│   └── src/main/java/com/yuetiku/
│       ├── entity/         # 答题记录实体
│       ├── dto/           # 答题相关DTO
│       ├── mapper/        # 答题数据访问
│       └── service/       # 答题业务逻辑
├── YueTiku-Sum/            # 统计模块
│   └── src/main/java/com/yuetiku/
│       ├── entity/         # 统计实体
│       ├── dto/           # 统计相关DTO
│       ├── mapper/        # 统计数据访问
│       └── service/       # 统计业务逻辑
└── YueTiku-Ai/             # AI模块（预留）
```

## 核心功能

### 1. 用户管理
- 用户注册/登录
- JWT身份认证
- 用户信息管理

### 2. 题库管理
- 分类管理（支持树形结构）
- 题目管理（支持多种题型）
- 题目选项和答案管理

### 3. 答题功能
- 随机获取题目
- 按分类获取题目
- 答案验证和评分
- 答题历史记录

### 4. 统计分析
- 统计概览
- 分类统计
- 时间线统计

## 支持的题型

- **单选题 (single)**: 从多个选项中选择一个正确答案
- **多选题 (multiple)**: 从多个选项中选择多个正确答案
- **填空题 (fill)**: 填写文本答案，支持模糊匹配
- **简答题 (answer)**: 填写文本答案，支持模糊匹配
- **判断题 (judge)**: 判断对错

## 数据库设计

### 核心表结构

1. **users** - 用户表
2. **categories** - 分类表
3. **questions** - 题目表
4. **question_options** - 题目选项表
5. **question_answers** - 题目答案表
6. **user_answers** - 用户答题记录表
7. **user_statistics** - 用户统计表


## API接口

### 用户管理接口
- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录
- `GET /user/info` - 获取用户信息

### 题库管理接口
- `GET /categories` - 获取分类列表
- `POST /categories` - 创建分类
- `PUT /categories/{id}` - 更新分类
- `DELETE /categories/{id}` - 删除分类
- `GET /questions` - 获取题目列表
- `POST /questions` - 创建题目
- `PUT /questions/{id}` - 更新题目
- `DELETE /questions/{id}` - 删除题目

### 答题接口
- `GET /quiz/random` - 随机获取题目
- `GET /quiz/category/{id}` - 按分类获取题目
- `POST /quiz/submit` - 提交答案
- `GET /quiz/history` - 获取答题历史

### 统计接口
- `GET /statistics/overview` - 获取统计概览
- `GET /statistics/category` - 获取分类统计
- `GET /statistics/timeline` - 获取时间线统计


## 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE yuetiku CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行数据库脚本
source 数据库创建说明.md
```

3. **配置应用**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yuetiku?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

4. **编译运行**
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

5. **访问接口**
```
基础URL: http://localhost:8080
```

## 配置说明

### 数据库配置
在 `application.yml` 中配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yuetiku
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### JWT配置
在 `application.yml` 中配置JWT密钥：

```yaml
jwt:
  secret: your-secret-key
  expiration: 86400  # 24小时
```

## 开发说明

### 项目特点
- **微服务架构**: 模块化设计，职责分离
- **多用户支持**: 每个用户独立的题目库和统计
- **灵活题型**: 支持5种常见题型
- **智能评分**: 支持模糊匹配和精确匹配
- **统计分析**: 提供详细的学习数据分析

### 开发规范
- 使用Lombok减少样板代码
- 统一异常处理
- RESTful API设计
- 分层架构设计

## 部署说明

### 生产环境部署
1. 修改数据库配置
2. 配置JWT密钥
3. 打包应用：`mvn clean package`
4. 运行JAR包：`java -jar target/yuetiku-admin-1.0.0.jar`

### Docker部署（可选）
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/yuetiku-admin-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 常见问题

### Q: 如何添加新的题型？
A: 在 `QuizServiceImpl.validateAnswer` 方法中添加新的题型验证逻辑。

### Q: 如何修改评分规则？
A: 在 `QuizServiceImpl.submitAnswer` 方法中修改评分逻辑。

### Q: 如何扩展统计功能？
A: 在 `StatisticsService` 中添加新的统计方法，并在 `StatisticsMapper.xml` 中实现对应的SQL查询。

## 更新日志

### v1.0.0 (2025-09-01)
- 初始版本发布
- 支持用户管理、题库管理、答题功能
- 支持5种题型
- 提供统计分析功能


---

**注意**: 这是一个学习项目，仅供学习和参考使用。
