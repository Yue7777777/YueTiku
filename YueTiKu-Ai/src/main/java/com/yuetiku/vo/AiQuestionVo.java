package com.yuetiku.vo;

import lombok.Data;

import java.util.List;

@Data
public class AiQuestionVo {
    /**
     * 题型：单选、多选、填空、简答、判断
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
     * 难度：easy、medium、hard
     */
    private String difficulty;

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
    public static class QuestionAnswerRequest {
        /**
         * 答案类型：选项、文本
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
