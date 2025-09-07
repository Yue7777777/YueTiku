package com.yuetiku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计概览实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_statistics")
public class StatisticsOverview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分类ID，NULL表示全部
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 总答题数
     */
    @TableField("total_questions")
    private Integer totalQuestions;

    /**
     * 正确答题数
     */
    @TableField("correct_answers")
    private Integer correctAnswers;

    /**
     * 总得分
     */
    @TableField("total_score")
    private BigDecimal totalScore;

    /**
     * 正确率
     */
    @TableField("accuracy_rate")
    private BigDecimal accuracyRate;

    /**
     * 最后答题时间
     */
    @TableField("last_answer_time")
    private LocalDateTime lastAnswerTime;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 分类名称（用于查询结果）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 用户名（用于排行榜）
     */
    @TableField(exist = false)
    private String username;

    /**
     * 用户头像（用于排行榜）
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 今日答题数（用于分类统计）
     */
    @TableField(exist = false)
    private Integer todayQuestions;

    /**
     * 本周答题数（用于分类统计）
     */
    @TableField(exist = false)
    private Integer weekQuestions;

    /**
     * 本月答题数（用于分类统计）
     */
    @TableField(exist = false)
    private Integer monthQuestions;

    /**
     * 学习时长（分钟）
     */
    @TableField(exist = false)
    private Integer studyMinutes;

    /**
     * 连续答题天数
     */
    @TableField(exist = false)
    private Integer consecutiveDays;

    /**
     * 答题日期（用于时间线统计）
     */
    @TableField(exist = false)
    private LocalDate answerDate;
}
