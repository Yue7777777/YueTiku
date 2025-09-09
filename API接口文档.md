# 用户登录注册API接口文档

## 基础信息
- 基础URL: `http://localhost:8080`
- 请求格式: `application/json`
- 响应格式: `application/json`

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1640995200000
}
```

## 接口列表

### 1. 用户注册

**接口地址**: `POST /user/register`

**请求参数**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456",
  "confirmPassword": "123456"
}
```

**参数说明**:
- `username`: 用户名，3-20位，只能包含字母、数字和下划线
- `email`: 邮箱地址，必须符合邮箱格式
- `password`: 密码，6-20位
- `confirmPassword`: 确认密码，必须与password一致

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "注册成功",
  "timestamp": 1640995200000
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 2. 用户登录

**接口地址**: `POST /user/login`

**请求参数**:
```json
{
  "email": "test@example.com",
  "password": "123456"
}
```

**参数说明**:
- `email`: 邮箱地址
- `password`: 密码

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "avatar": null,
      "status": 1
    }
  },
  "timestamp": 1640995200000
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 3. 获取当前用户信息

**接口地址**: `GET /user/info`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatar": null,
    "status": 1
  },
  "timestamp": 1640995200000
}
```

**错误响应**:
```json
{
  "code": 401,
  "message": "用户未登录",
  "data": null,
  "timestamp": 1640995200000
}
```

```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

```json
{
  "code": 403,
  "message": "用户已被禁用",
  "data": null,
  "timestamp": 1640995200000
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |

## 使用说明

1. **注册流程**: 先调用注册接口创建用户账号
2. **登录流程**: 使用邮箱和密码登录，获取访问令牌
3. **访问受保护资源**: 在请求头中携带 `Authorization: Bearer <accessToken>`

## 安全说明

- 密码使用BCrypt加密存储
- JWT令牌包含用户ID、用户名和用户类型信息
- 访问令牌默认24小时过期
- 刷新令牌默认7天过期
- 所有接口都支持CORS跨域请求

---

# 题库管理API接口文档

## 基础信息
- 基础URL: `http://localhost:8080`
- 请求格式: `application/json`
- 响应格式: `application/json`

## 题目分类管理接口

> **注意**: 所有分类管理接口都基于当前登录用户，每个用户都有独立的分类体系。

### 1. 获取分类列表

**接口地址**: `GET /categories`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 获取当前登录用户的所有分类，返回扁平化的分类列表（包括顶级分类和子分类）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取分类列表成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "name": "Java基础",
      "description": "Java基础知识",
      "sortOrder": 1,
      "status": 1,
      "userId": 1,
      "createdAt": "2024-01-01T10:00:00",
      "children": null
    },
    {
      "id": 2,
      "parentId": 1,
      "name": "Java语法",
      "description": "Java语法相关题目",
      "sortOrder": 1,
      "status": 1,
      "userId": 1,
      "createdAt": "2024-01-01T10:00:00",
      "children": null
    },
    {
      "id": 3,
      "parentId": 1,
      "name": "Java集合",
      "description": "Java集合框架",
      "sortOrder": 2,
      "status": 1,
      "userId": 1,
      "createdAt": "2024-01-01T10:00:00",
      "children": null
    }
  ],
  "timestamp": 1640995200000
}
```

### 2. 根据ID获取分类详情

**接口地址**: `GET /categories/{id}`

**请求参数**:
- `id`: 分类ID（路径参数）

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 根据分类ID获取当前登录用户的分类详情

**响应示例**:
```json
{
  "code": 200,
  "message": "获取分类详情成功",
  "data": {
    "id": 1,
    "parentId": 0,
    "name": "Java基础",
    "description": "Java基础知识",
    "sortOrder": 1,
    "status": 1,
    "userId": 1,
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

### 3. 获取子分类列表

**接口地址**: `GET /categories/children?parentId={parentId}`

**请求参数**:
- `parentId`: 父分类ID（必须是当前用户的分类）

**功能说明**: 获取当前登录用户指定父分类下的子分类列表

**响应示例**:
```json
{
  "code": 200,
  "message": "获取子分类列表成功",
  "data": [
    {
      "id": 2,
      "parentId": 1,
      "name": "Java语法",
      "description": "Java语法相关题目",
      "sortOrder": 1,
      "status": 1,
      "userId": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000
}
```

### 4. 创建分类

**接口地址**: `POST /categories`

**功能说明**: 为当前登录用户创建新的分类

**请求参数**:
```json
{
  "parentId": 0,
  "name": "Java基础",
  "description": "Java基础知识",
  "sortOrder": 1,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建分类成功",
  "data": {
    "id": 1,
    "parentId": 0,
    "name": "Java基础",
    "description": "Java基础知识",
    "sortOrder": 1,
    "status": 1,
    "userId": 1,
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

### 5. 更新分类

**接口地址**: `PUT /categories/{id}`

**功能说明**: 更新当前登录用户的指定分类

**请求参数**:
```json
{
  "parentId": 0,
  "name": "Java基础（更新）",
  "description": "Java基础知识（更新）",
  "sortOrder": 1,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新分类成功",
  "data": {
    "id": 1,
    "parentId": 0,
    "name": "Java基础（更新）",
    "description": "Java基础知识（更新）",
    "sortOrder": 1,
    "status": 1,
    "userId": 1,
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

### 6. 删除分类

**接口地址**: `DELETE /categories/{id}`

**功能说明**: 删除当前登录用户的指定分类

**响应示例**:
```json
{
  "code": 200,
  "message": "删除分类成功",
  "data": "删除分类成功",
  "timestamp": 1640995200000
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "分类不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

```json
{
  "code": 500,
  "message": "该分类下还有子分类，无法删除",
  "data": null,
  "timestamp": 1640995200000
}
```

## 题目管理接口

> **注意**: 所有题目管理接口都基于当前登录用户，每个用户都有独立的题目库。

### 1. 获取题目列表（分页）

**接口地址**: `GET /questions?page=1&size=10&categoryId=1&type=single&difficulty=easy`

**功能说明**: 获取当前登录用户的题目列表，支持分页和条件筛选

**请求参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `categoryId`: 分类ID（可选，必须是当前用户的分类）
- `type`: 题型（可选）
- `difficulty`: 难度（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目列表成功",
  "data": {
    "records": [
      {
        "id": 1,
        "categoryId": 1,
        "type": "single",
        "title": "Java中哪个关键字用于继承？",
        "content": "在Java中，用于实现继承的关键字是：",
        "difficulty": "easy",
        "points": 1,
        "status": 1,
        "createdBy": 1,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  },
  "timestamp": 1640995200000
}
```

### 2. 获取题目详情

**接口地址**: `GET /questions/{id}`

**功能说明**: 获取指定题目的详细信息（包括选项和答案）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目详情成功",
  "data": {
    "id": 1,
    "categoryId": 1,
    "categoryName": "Java基础",
    "type": "single",
    "title": "Java中哪个关键字用于继承？",
    "content": "在Java中，用于实现继承的关键字是：",
    "explanation": "extends关键字用于实现类的继承",
    "difficulty": "easy",
    "points": 1,
    "status": 1,
    "createdBy": 1,
    "createdAt": "2024-01-01T10:00:00",
    "options": [
      {
        "id": 1,
        "optionKey": "A",
        "optionContent": "extends",
        "isCorrect": true,
        "sortOrder": 1
      },
      {
        "id": 2,
        "optionKey": "B",
        "optionContent": "implements",
        "isCorrect": false,
        "sortOrder": 2
      }
    ],
    "answer": {
      "id": 1,
      "answerType": "option",
      "correctAnswer": "A",
      "answerExplanation": "extends关键字用于实现类的继承"
    }
  },
  "timestamp": 1640995200000
}
```

### 3. 根据分类获取题目列表

**接口地址**: `GET /questions/category/{categoryId}`

**功能说明**: 获取当前登录用户指定分类下的题目列表

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目列表成功",
  "data": [
    {
      "id": 1,
      "categoryId": 1,
      "type": "single",
      "title": "Java中哪个关键字用于继承？",
      "difficulty": "easy",
      "points": 1,
      "status": 1,
      "createdBy": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000
}
```

### 4. 根据题型获取题目列表

**接口地址**: `GET /questions/type/{type}`

**功能说明**: 获取当前登录用户指定题型的题目列表

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目列表成功",
  "data": [
    {
      "id": 1,
      "categoryId": 1,
      "type": "single",
      "title": "Java中哪个关键字用于继承？",
      "difficulty": "easy",
      "points": 1,
      "status": 1,
      "createdBy": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000
}
```

### 5. 根据难度获取题目列表

**接口地址**: `GET /questions/difficulty/{difficulty}`

**功能说明**: 获取当前登录用户指定难度的题目列表

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目列表成功",
  "data": [
    {
      "id": 1,
      "categoryId": 1,
      "type": "single",
      "title": "Java中哪个关键字用于继承？",
      "difficulty": "easy",
      "points": 1,
      "status": 1,
      "createdBy": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000
}
```

### 6. 创建题目

**接口地址**: `POST /questions`

**功能说明**: 为当前登录用户创建新的题目

**请求参数**:
```json
{
  "categoryId": 1,
  "type": "single",
  "title": "Java中哪个关键字用于继承？",
  "content": "在Java中，用于实现继承的关键字是：",
  "explanation": "extends关键字用于实现类的继承",
  "difficulty": "easy",
  "points": 1,
  "source": "Java基础教程",
  "tags": "Java,继承,关键字",
  "status": 1,
  "options": [
    {
      "optionKey": "A",
      "optionContent": "extends",
      "isCorrect": true,
      "sortOrder": 1
    },
    {
      "optionKey": "B",
      "optionContent": "implements",
      "isCorrect": false,
      "sortOrder": 2
    }
  ],
  "answer": {
    "answerType": "option",
    "correctAnswer": "A",
    "answerExplanation": "extends关键字用于实现类的继承"
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建题目成功",
  "data": {
    "id": 1,
    "categoryId": 1,
    "type": "single",
    "title": "Java中哪个关键字用于继承？",
    "content": "在Java中，用于实现继承的关键字是：",
    "difficulty": "easy",
    "points": 1,
    "status": 1,
    "createdBy": 1,
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

### 7. 更新题目

**接口地址**: `PUT /questions/{id}`

**功能说明**: 更新当前登录用户的指定题目

**请求参数**: 同创建题目

**响应示例**:
```json
{
  "code": 200,
  "message": "更新题目成功",
  "data": {
    "id": 1,
    "categoryId": 1,
    "type": "single",
    "title": "Java中哪个关键字用于继承？（更新）",
    "content": "在Java中，用于实现继承的关键字是：",
    "difficulty": "easy",
    "points": 1,
    "status": 1,
    "createdBy": 1,
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

### 8. 删除题目

**接口地址**: `DELETE /questions/{id}`

**功能说明**: 删除当前登录用户的指定题目

**响应示例**:
```json
{
  "code": 200,
  "message": "删除题目成功",
  "data": "删除题目成功",
  "timestamp": 1640995200000
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "题目不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

## 题型说明

- `single`: 单选题
- `multiple`: 多选题
- `fill`: 填空题
- `answer`: 简答题
- `judge`: 判断题

## 难度说明

- `easy`: 简单
- `medium`: 中等
- `hard`: 困难

## 答案类型说明

- `option`: 选项答案（单选、多选、判断题）
- `text`: 文本答案（填空题、简答题）

---

# 用户答题API接口文档

## 基础信息
- 基础URL: `http://localhost:8080`
- 请求格式: `application/json`
- 响应格式: `application/json`

## 答题管理接口

> **注意**: 所有答题接口都基于当前登录用户，需要携带有效的JWT令牌。

### 1. 随机获取题目

**接口地址**: `GET /quiz/random?categoryId=1&difficulty=easy&count=1`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 随机获取题目，支持按分类和难度筛选

**请求参数**:
- `categoryId`: 分类ID（可选）
- `difficulty`: 难度（可选，easy/medium/hard）
- `count`: 题目数量（默认100）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取随机题目成功",
  "data": {
    "id": 1,
    "categoryId": 1,
    "categoryName": "Java基础",
    "type": "single",
    "title": "Java中哪个关键字用于继承？",
    "content": "在Java中，用于实现继承的关键字是：",
    "difficulty": "easy",
    "points": 1,
    "source": "Java基础教程",
    "tags": "Java,继承,关键字",
    "count": 1,
    "options": [
      {
        "id": 1,
        "optionKey": "A",
        "optionContent": "extends",
        "sortOrder": 1
      },
      {
        "id": 2,
        "optionKey": "B",
        "optionContent": "implements",
        "sortOrder": 2
      }
    ]
  },
  "timestamp": 1640995200000
}
```

### 2. 按分类获取题目

**接口地址**: `GET /quiz/category/{categoryId}?difficulty=easy&count=1`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 按指定分类获取题目

**请求参数**:
- `categoryId`: 分类ID（路径参数）
- `difficulty`: 难度（可选）
- `count`: 题目数量（默认100）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取分类题目成功",
  "data": {
    "id": 1,
    "categoryId": 1,
    "categoryName": "Java基础",
    "type": "single",
    "title": "Java中哪个关键字用于继承？",
    "content": "在Java中，用于实现继承的关键字是：",
    "difficulty": "easy",
    "points": 1,
    "count": 1,
    "options": [
      {
        "id": 1,
        "optionKey": "A",
        "optionContent": "extends",
        "sortOrder": 1
      }
    ]
  },
  "timestamp": 1640995200000
}
```

### 3. 提交答案

**接口地址**: `POST /quiz/submit`

**请求头**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**功能说明**: 提交用户答案并获取答题结果

**请求参数**:
```json
{
  "questionId": 1,
  "userAnswer": "A"
}
```

**参数说明**:
- `questionId`: 题目ID（必填）
- `userAnswer`: 用户答案（必填）

**响应示例**:
```json
{
  "code": 200,
  "message": "提交答案成功",
  "data": {
    "questionId": 1,
    "userAnswer": "A",
    "correctAnswer": "A",
    "isCorrect": true,
    "score": 1.0,
    "explanation": "extends关键字用于实现类的继承",
    "answerTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "题目不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 4. 获取答题历史

**接口地址**: `GET /quiz/history?page=1&size=10&categoryId=1`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 获取用户的答题历史记录

**请求参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `categoryId`: 分类ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取答题历史成功",
  "data": [
    {
      "id": 1,
      "questionId": 1,
      "questionTitle": "Java中哪个关键字用于继承？",
      "categoryName": "Java基础",
      "type": "single",
      "difficulty": "easy",
      "userAnswer": "A",
      "isCorrect": true,
      "score": 1.0,
      "answerTime": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000
}
```

### 5. 获取用户统计信息

**接口地址**: `GET /quiz/statistics?categoryId=1`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 获取用户的答题统计信息

**请求参数**:
- `categoryId`: 分类ID（可选，不传则获取全部分类统计）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取统计信息成功",
  "data": {
    "categoryId": 1,
    "categoryName": "Java基础",
    "totalQuestions": 10,
    "correctAnswers": 8,
    "totalScore": 8.0,
    "accuracyRate": 80.0,
    "lastAnswerTime": "2024-01-01T10:00:00",
    "averageScore": 0.8,
    "consecutiveCorrect": 3,
    "todayQuestions": 5,
    "weekQuestions": 20,
    "monthQuestions": 80
  },
  "timestamp": 1640995200000
}
```

**全部分类统计响应示例**:
```json
{
  "code": 200,
  "message": "获取统计信息成功",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "Java基础",
      "totalQuestions": 10,
      "correctAnswers": 8,
      "totalScore": 8.0,
      "accuracyRate": 80.0,
      "lastAnswerTime": "2024-01-01T10:00:00"
    },
    {
      "categoryId": 2,
      "categoryName": "Java进阶",
      "totalQuestions": 5,
      "correctAnswers": 3,
      "totalScore": 3.0,
      "accuracyRate": 60.0,
      "lastAnswerTime": "2024-01-01T09:30:00"
    }
  ],
  "timestamp": 1640995200000
}
```

## 答题流程说明

1. **选择答题模式**: 用户可以选择随机答题或按分类答题
2. **获取题目**: 调用随机获取题目或按分类获取题目接口
3. **填写答案**: 用户根据题目类型填写相应答案
4. **提交答案**: 调用提交答案接口，系统自动验证答案
5. **查看结果**: 系统返回答题结果，包括是否正确、得分、解析等
6. **记录统计**: 系统自动记录答题记录并更新用户统计信息

## 题型支持

- **单选题 (single)**: 用户选择单个选项，答案格式如 "A"
- **多选题 (multiple)**: 用户选择多个选项，答案格式如 "AB" 或 "ACD"
- **填空题 (fill)**: 用户填写文本答案，支持模糊匹配
- **简答题 (answer)**: 用户填写文本答案，支持模糊匹配
- **判断题 (judge)**: 用户选择对错，答案格式如 "A"（对）或 "B"（错）

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |

## 使用说明

1. **答题前准备**: 确保用户已登录并获取有效的JWT令牌
2. **获取题目**: 根据需求选择随机获取或按分类获取题目
3. **提交答案**: 根据题目类型正确填写答案格式
4. **查看历史**: 可以查看历史答题记录和统计信息
5. **统计分析**: 系统提供详细的答题统计和数据分析

---

# 统计模块API接口文档

## 基础信息
- 基础URL: `http://localhost:8080`
- 请求格式: `application/json`
- 响应格式: `application/json`

## 统计管理接口

> **注意**: 所有统计接口都基于当前登录用户，需要携带有效的JWT令牌。

### 1. 获取统计概览

**接口地址**: `GET /statistics/overview`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 获取用户的统计概览信息，包括总答题数、正确率、学习时长等

**响应示例**:
```json
{
  "code": 200,
  "message": "获取统计概览成功",
  "data": {
    "totalQuestions": 150,
    "correctAnswers": 120,
    "totalScore": 120.0,
    "accuracyRate": 80.0,
    "averageScore": 0.8,
    "lastAnswerTime": "2024-01-01T10:00:00",
    "todayQuestions": 5,
    "weekQuestions": 25,
    "monthQuestions": 80,
    "consecutiveDays": 7,
    "totalStudyMinutes": 300,
    "averageDailyQuestions": 21.43
  },
  "timestamp": 1640995200000
}
```

### 2. 按分类统计

**接口地址**: `GET /statistics/category`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 获取用户按分类的统计信息

**响应示例**:
```json
{
  "code": 200,
  "message": "获取分类统计成功",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "Java基础",
      "totalQuestions": 50,
      "correctAnswers": 40,
      "totalScore": 40.0,
      "accuracyRate": 80.0,
      "averageScore": 0.8,
      "lastAnswerTime": "2024-01-01T10:00:00",
      "todayQuestions": 2,
      "weekQuestions": 10,
      "monthQuestions": 30,
      "ranking": 1
    },
    {
      "categoryId": 2,
      "categoryName": "Java进阶",
      "totalQuestions": 30,
      "correctAnswers": 24,
      "totalScore": 24.0,
      "accuracyRate": 80.0,
      "averageScore": 0.8,
      "lastAnswerTime": "2024-01-01T09:30:00",
      "todayQuestions": 1,
      "weekQuestions": 5,
      "monthQuestions": 15,
      "ranking": 2
    }
  ],
  "timestamp": 1640995200000
}
```

### 3. 时间线统计

**接口地址**: `GET /statistics/timeline?days=30`

**请求头**:
```
Authorization: Bearer <accessToken>
```

**功能说明**: 获取用户的时间线统计信息，按日期显示答题情况

**请求参数**:
- `days`: 统计天数（默认30天）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取时间线统计成功",
  "data": [
    {
      "date": "2024-01-01",
      "questionCount": 5,
      "correctCount": 4,
      "score": 4.0,
      "accuracyRate": 80.0,
      "studyMinutes": 10,
      "consecutiveDays": 1
    },
    {
      "date": "2023-12-31",
      "questionCount": 3,
      "correctCount": 2,
      "score": 2.0,
      "accuracyRate": 66.67,
      "studyMinutes": 6,
      "consecutiveDays": 2
    }
  ],
  "timestamp": 1640995200000
}
```


## 统计功能说明

### 统计概览功能
- **总答题数**: 用户累计答题总数
- **正确答题数**: 用户累计答对题目数
- **总得分**: 用户累计获得的总分数
- **正确率**: 正确答题数 / 总答题数 * 100%
- **平均分**: 总得分 / 总答题数
- **今日答题数**: 当天答题数量
- **本周答题数**: 本周答题数量
- **本月答题数**: 本月答题数量
- **连续答题天数**: 连续答题的天数
- **学习总时长**: 累计学习时间（分钟）
- **平均每日答题数**: 总答题数 / 连续答题天数

### 分类统计功能
- 按分类显示用户的答题统计
- 包含每个分类的答题数、正确率、得分等
- 支持分类排名显示

### 时间线统计功能
- 按日期显示用户的答题情况
- 支持自定义统计天数
- 显示每日答题数、正确率、学习时长等


## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |

## 使用说明

1. **统计概览**: 获取用户的整体学习统计情况
2. **分类统计**: 查看各分类的学习进度和成绩
3. **时间线统计**: 分析学习趋势和进度变化
