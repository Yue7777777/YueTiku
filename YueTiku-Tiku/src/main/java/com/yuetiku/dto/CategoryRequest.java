package com.yuetiku.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
public class CategoryRequest {

    /**
     * 父分类ID，0表示顶级分类
     */
    @NotNull(message = "父分类ID不能为空")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    /**
     * 分类描述
     */
    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：1-启用，0-禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}

