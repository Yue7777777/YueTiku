package com.yuetiku.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuetiku.dto.CategoryRequest;
import com.yuetiku.entity.Category;

import java.util.List;

/**
 * 题目分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 创建分类
     *
     * @param categoryRequest 分类请求
     * @return 分类信息
     */
    Category createCategory(CategoryRequest categoryRequest);

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param categoryRequest 分类请求
     * @return 分类信息
     */
    Category updateCategory(Long id, CategoryRequest categoryRequest);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    boolean deleteCategory(Long id);

    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    List<Category> getCategoryTree();

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getChildrenCategories(Long parentId);
}

