package com.yuetiku.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分类统计响应DTO
 */
@Data
public class StatisticsCategoryResponse {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 总答题数
     */
    private Integer totalQuestions;

    /**
     * 正确答题数
     */
    private Integer correctAnswers;

    /**
     * 总得分
     */
    private BigDecimal totalScore;

    /**
     * 正确率
     */
    private BigDecimal accuracyRate;

    /**
     * 平均分
     */
    private BigDecimal averageScore;

    /**
     * 最后答题时间
     */
    private LocalDateTime lastAnswerTime;

    /**
     * 今日答题数
     */
    private Integer todayQuestions;

    /**
     * 本周答题数
     */
    private Integer weekQuestions;

    /**
     * 本月答题数
     */
    private Integer monthQuestions;

    /**
     * 排名
     */
    private Integer ranking;
}
