package com.yuetiku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户答题记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_answers")
public class UserAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 答题记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 题目ID
     */
    @TableField("question_id")
    private Long questionId;

    /**
     * 用户答案
     */
    @TableField("user_answer")
    private String userAnswer;

    /**
     * 是否正确
     */
    @TableField("is_correct")
    private Boolean isCorrect;

    /**
     * 得分
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 答题时间
     */
    @TableField("answer_time")
    private LocalDateTime answerTime;
}
