package com.yuetiku.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 题目请求DTO
 */
@Data
public class QuestionRequest {

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 题型：单选、多选、填空、简答、判断
     */
    @NotBlank(message = "题型不能为空")
    private String type;

    /**
     * 题目标题
     */
    @NotBlank(message = "题目标题不能为空")
    @Size(max = 1000, message = "题目标题长度不能超过1000个字符")
    private String title;

    /**
     * 题目内容
     */
    @Size(max = 2000, message = "题目内容长度不能超过2000个字符")
    private String content;

    /**
     * 题目解析
     */
    @Size(max = 2000, message = "题目解析长度不能超过2000个字符")
    private String explanation;

    /**
     * 难度：easy、medium、hard
     */
    @NotBlank(message = "难度不能为空")
    private String difficulty;

    /**
     * 分值
     */
    @NotNull(message = "分值不能为空")
    private Integer points;

    /**
     * 题目来源
     */
    @Size(max = 200, message = "题目来源长度不能超过200个字符")
    private String source;

    /**
     * 题目标签，逗号分隔
     */
    @Size(max = 500, message = "题目标签长度不能超过500个字符")
    private String tags;

    /**
     * 状态：1-正常，0-禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 题目选项列表
     */
    private List<QuestionOptionRequest> options;

    /**
     * 题目答案
     */
    private QuestionAnswerRequest answer;

    @Data
    public static class QuestionOptionRequest {
        /**
         * 选项标识：A、B、C、D
         */
        @NotBlank(message = "选项标识不能为空")
        private String optionKey;

        /**
         * 选项内容
         */
        @NotBlank(message = "选项内容不能为空")
        private String optionContent;

        /**
         * 是否为正确答案
         */
        @NotNull(message = "是否正确答案不能为空")
        private Boolean isCorrect;

        /**
         * 排序
         */
        private Integer sortOrder;
    }

    @Data
    public static class QuestionAnswerRequest {
        /**
         * 答案类型：选项、文本
         */
        @NotBlank(message = "答案类型不能为空")
        private String answerType;

        /**
         * 标准答案
         */
        @NotBlank(message = "标准答案不能为空")
        private String correctAnswer;

        /**
         * 答案解释
         */
        private String answerExplanation;
    }
}

