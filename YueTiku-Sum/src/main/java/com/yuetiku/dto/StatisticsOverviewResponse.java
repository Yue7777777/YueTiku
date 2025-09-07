package com.yuetiku.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统计概览响应DTO
 */
@Data
public class StatisticsOverviewResponse {

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
     * 连续答题天数
     */
    private Integer consecutiveDays;

    /**
     * 学习总时长（分钟）
     */
    private Integer totalStudyMinutes;

    /**
     * 平均每日答题数
     */
    private BigDecimal averageDailyQuestions;
}
