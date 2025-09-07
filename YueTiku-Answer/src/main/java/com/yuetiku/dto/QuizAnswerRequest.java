package com.yuetiku.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 答题请求DTO
 */
@Data
public class QuizAnswerRequest {

    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;
}
