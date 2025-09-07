package com.yuetiku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 题目选项实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("question_options")
public class QuestionOption {

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
     * 选项标识：A、B、C、D
     */
    @TableField("option_key")
    private String optionKey;

    /**
     * 选项内容
     */
    @TableField("option_content")
    private String optionContent;

    /**
     * 是否为正确答案
     */
    @TableField("is_correct")
    private Boolean isCorrect;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

