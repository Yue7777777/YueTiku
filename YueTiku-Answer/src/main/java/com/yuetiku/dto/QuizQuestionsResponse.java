package com.yuetiku.dto;

import lombok.Data;

import java.util.List;

/**
 * 答题题目列表响应DTO
 */
@Data
public class QuizQuestionsResponse {

    /**
     * 题目列表
     */
    private List<QuizQuestionResponse> questions;

    /**
     * 题目数量
     */
    private Integer count;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;
}
