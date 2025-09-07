package com.yuetiku.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目详情响应DTO
 */
@Data
public class QuestionDetailResponse {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 题型
     */
    private String type;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目解析
     */
    private String explanation;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 分值
     */
    private Integer points;

    /**
     * 题目来源
     */
    private String source;

    /**
     * 题目标签
     */
    private String tags;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 题目选项列表
     */
    private List<QuestionOptionResponse> options;

    /**
     * 题目答案
     */
    private QuestionAnswerResponse answer;

    @Data
    public static class QuestionOptionResponse {
        /**
         * 选项ID
         */
        private Long id;

        /**
         * 选项标识
         */
        private String optionKey;

        /**
         * 选项内容
         */
        private String optionContent;

        /**
         * 是否为正确答案
         */
        private Boolean isCorrect;

        /**
         * 排序
         */
        private Integer sortOrder;
    }

    @Data
    public static class QuestionAnswerResponse {
        /**
         * 答案ID
         */
        private Long id;

        /**
         * 答案类型
         */
        private String answerType;

        /**
         * 标准答案
         */
        private String correctAnswer;

        /**
         * 答案解释
         */
        private String answerExplanation;
    }
}

