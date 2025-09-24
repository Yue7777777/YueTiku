package com.yuetiku.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答题题目JOIN查询结果DTO
 */
@Data
public class QuizQuestionWithJoinsDto {
    
    // 题目基本信息
    private Long id;
    private Long categoryId;
    private String type;
    private String title;
    private String content;
    private String explanation;
    private String difficulty;
    private Integer points;
    private String source;
    private String tags;
    private Integer status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 分类信息
    private String categoryName;
    
    // 选项信息
    private Long optionId;
    private String optionKey;
    private String optionContent;
    private Boolean isCorrect;
    private Integer sortOrder;
    private LocalDateTime optionCreatedAt;
}
