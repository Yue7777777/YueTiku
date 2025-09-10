package com.yuetiku.controller;

import com.yuetiku.common.Result;
import com.yuetiku.dto.CategoryRequest;
import com.yuetiku.entity.Category;
import com.yuetiku.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<Category>> getCategories() {
        log.info("获取当前用户分类列表");
        List<Category> categories = categoryService.getCategoryTree();
        return Result.success("获取分类列表成功", categories);
    }

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/children")
    public Result<List<Category>> getChildrenCategories(@RequestParam Long parentId) {
        log.info("获取当前用户子分类列表，父分类ID: {}", parentId);
        List<Category> categories = categoryService.getChildrenCategories(parentId);
        return Result.success("获取子分类列表成功", categories);
    }

    /**
     * 创建分类
     *
     * @param categoryRequest 分类请求
     * @return 分类信息
     */
    @PostMapping
    public Result<Category> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        log.info("创建当前用户分类: {}", categoryRequest.getName());
        Category category = categoryService.createCategory(categoryRequest);
        return Result.success("创建分类成功", category);
    }

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param categoryRequest 分类请求
     * @return 分类信息
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        log.info("更新当前用户分类，ID: {}", id);
        Category category = categoryService.updateCategory(id, categoryRequest);
        return Result.success("更新分类成功", category);
    }

    /**
     * 根据ID获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        log.info("获取当前用户分类详情，ID: {}", id);
        Category category = categoryService.getCategoryById(id);
        return Result.success("获取分类详情成功", category);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        log.info("删除当前用户分类，ID: {}", id);
        categoryService.deleteCategory(id);
        return Result.success("删除分类成功");
    }
}
