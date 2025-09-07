package com.yuetiku.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答题历史响应DTO
 */
@Data
public class QuizHistoryResponse {

    /**
     * 答题记录ID
     */
    private Long id;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 题目标题
     */
    private String questionTitle;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 题型
     */
    private String type;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 答题时间
     */
    private LocalDateTime answerTime;
}
