package com.yuetiku.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答题响应DTO
 */
@Data
public class QuizAnswerResponse {

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 题目解析
     */
    private String explanation;

    /**
     * 答题时间
     */
    private LocalDateTime answerTime;
}
