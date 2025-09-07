package com.yuetiku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuetiku.context.BaseContext;
import com.yuetiku.dto.CategoryRequest;
import com.yuetiku.entity.Category;
import com.yuetiku.mapper.CategoryMapper;
import com.yuetiku.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(CategoryRequest categoryRequest) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查父分类是否存在（必须是当前用户的分类）
        if (categoryRequest.getParentId() != 0) {
            LambdaQueryWrapper<Category> parentQueryWrapper = new LambdaQueryWrapper<>();
            parentQueryWrapper.eq(Category::getId, categoryRequest.getParentId())
                    .eq(Category::getUserId, currentUserId);
            Category parentCategory = getOne(parentQueryWrapper);
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
        }

        // 检查同级分类名称是否重复（必须是当前用户的分类）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, categoryRequest.getParentId())
                .eq(Category::getName, categoryRequest.getName())
                .eq(Category::getUserId, currentUserId);
        Category existingCategory = getOne(queryWrapper);
        if (existingCategory != null) {
            throw new RuntimeException("同级分类名称已存在");
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        category.setUserId(currentUserId);
        category.setCreatedAt(LocalDateTime.now());

        boolean saved = save(category);
        if (!saved) {
            throw new RuntimeException("创建分类失败");
        }

        log.info("创建分类成功: {}, 用户ID: {}", category.getName(), currentUserId);
        return category;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查分类是否存在且属于当前用户
        LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(Category::getId, id)
                .eq(Category::getUserId, currentUserId);
        Category category = getOne(categoryQueryWrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查父分类是否存在（必须是当前用户的分类）
        if (categoryRequest.getParentId() != 0) {
            LambdaQueryWrapper<Category> parentQueryWrapper = new LambdaQueryWrapper<>();
            parentQueryWrapper.eq(Category::getId, categoryRequest.getParentId())
                    .eq(Category::getUserId, currentUserId);
            Category parentCategory = getOne(parentQueryWrapper);
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
        }

        // 检查同级分类名称是否重复（排除自己，必须是当前用户的分类）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, categoryRequest.getParentId())
                .eq(Category::getName, categoryRequest.getName())
                .eq(Category::getUserId, currentUserId)
                .ne(Category::getId, id);
        Category existingCategory = getOne(queryWrapper);
        if (existingCategory != null) {
            throw new RuntimeException("同级分类名称已存在");
        }

        BeanUtils.copyProperties(categoryRequest, category);
        category.setId(id);
        category.setUserId(currentUserId);

        boolean updated = updateById(category);
        if (!updated) {
            throw new RuntimeException("更新分类失败");
        }

        log.info("更新分类成功: {}, 用户ID: {}", category.getName(), currentUserId);
        return category;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查分类是否存在且属于当前用户
        LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(Category::getId, id)
                .eq(Category::getUserId, currentUserId);
        Category category = getOne(categoryQueryWrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查是否有子分类（必须是当前用户的分类）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, id)
                .eq(Category::getUserId, currentUserId);
        long childCount = count(queryWrapper);
        if (childCount > 0) {
            throw new RuntimeException("该分类下还有子分类，无法删除");
        }

        // TODO: 检查是否有题目关联该分类
        // 这里可以添加检查题目关联的逻辑

        boolean deleted = removeById(id);
        if (!deleted) {
            throw new RuntimeException("删除分类失败");
        }

        log.info("删除分类成功: {}, 用户ID: {}", category.getName(), currentUserId);
        return true;
    }

    @Override
    public List<Category> getCategoryTree() {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 获取当前用户的所有分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getUserId, currentUserId)
                .orderByAsc(Category::getSortOrder, Category::getId);
        List<Category> allCategories = list(queryWrapper);
        
        // 构建树形结构
        return buildCategoryTree(allCategories, 0L);
    }

    @Override
    public List<Category> getChildrenCategories(Long parentId) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, parentId)
                .eq(Category::getUserId, currentUserId)
                .orderByAsc(Category::getSortOrder, Category::getId);
        return list(queryWrapper);
    }

    /**
     * 构建分类树形结构
     */
    private List<Category> buildCategoryTree(List<Category> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .peek(category -> {
                    List<Category> children = buildCategoryTree(allCategories, category.getId());
                    // 这里可以设置children属性，但Category实体类中没有children字段
                    // 如果需要树形结构，可以创建一个包含children的DTO类
                })
                .toList();
    }
}
