package com.yuetiku.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 时间线统计响应DTO
 */
@Data
public class StatisticsTimelineResponse {

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 答题数
     */
    private Integer questionCount;

    /**
     * 正确数
     */
    private Integer correctCount;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 正确率
     */
    private BigDecimal accuracyRate;

    /**
     * 学习时长（分钟）
     */
    private Integer studyMinutes;

    /**
     * 连续答题天数
     */
    private Integer consecutiveDays;
}
