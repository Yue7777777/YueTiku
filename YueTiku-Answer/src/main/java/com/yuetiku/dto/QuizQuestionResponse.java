package com.yuetiku.dto;

import lombok.Data;

import java.util.List;

/**
 * 答题题目响应DTO
 */
@Data
public class QuizQuestionResponse {

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
     * 题目选项（选择题）
     */
    private List<QuizOptionResponse> options;

    /**
     * 题目数量
     */
    private Integer count;

    /**
     * 选项响应DTO
     */
    @Data
    public static class QuizOptionResponse {
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
         * 排序
         */
        private Integer sortOrder;
    }
}
