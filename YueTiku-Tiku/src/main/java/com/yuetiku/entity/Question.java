package com.yuetiku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 题目实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("questions")
public class Question {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 题型：单选、多选、填空、简答、判断
     */
    @TableField("type")
    private String type;

    /**
     * 题目标题
     */
    @TableField("title")
    private String title;

    /**
     * 题目内容
     */
    @TableField("content")
    private String content;

    /**
     * 题目解析
     */
    @TableField("explanation")
    private String explanation;

    /**
     * 难度：easy、medium、hard
     */
    @TableField("difficulty")
    private String difficulty;

    /**
     * 分值
     */
    @TableField("points")
    private Integer points;

    /**
     * 题目来源
     */
    @TableField("source")
    private String source;

    /**
     * 题目标签，逗号分隔
     */
    @TableField("tags")
    private String tags;

    /**
     * 状态：1-正常，0-禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建者ID
     */
    @TableField("created_by")
    private Long createdBy;

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
}

