package com.yuetiku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 题目答案实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("question_answers")
public class QuestionAnswer {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目ID
     */
    @TableField("question_id")
    private Long questionId;

    /**
     * 答案类型：选项、文本
     */
    @TableField("answer_type")
    private String answerType;

    /**
     * 标准答案
     */
    @TableField("correct_answer")
    private String correctAnswer;

    /**
     * 答案解释
     */
    @TableField("answer_explanation")
    private String answerExplanation;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

