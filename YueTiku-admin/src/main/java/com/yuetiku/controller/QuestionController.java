package com.yuetiku.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuetiku.common.Result;
import com.yuetiku.dto.QuestionDetailResponse;
import com.yuetiku.dto.QuestionRequest;
import com.yuetiku.entity.Question;
import com.yuetiku.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目控制器
 */
@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 获取题目列表（分页）
     *
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID
     * @param type 题型
     * @param difficulty 难度
     * @return 题目列表
     */
    @GetMapping
    public Result<IPage<Question>> getQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty) {
        log.info("获取当前用户题目列表，页码: {}, 每页大小: {}, 分类ID: {}, 题型: {}, 难度: {}", 
                page, size, categoryId, type, difficulty);
        
        IPage<Question> questions = questionService.getCurrentUserQuestions(page, size, categoryId, type, difficulty);
        
        return Result.success("获取题目列表成功", questions);
    }

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    @GetMapping("/{id}")
    public Result<QuestionDetailResponse> getQuestionDetail(@PathVariable Long id) {
        log.info("获取题目详情，ID: {}", id);
        QuestionDetailResponse question = questionService.getQuestionDetail(id);
        return Result.success("获取题目详情成功", question);
    }

    /**
     * 根据分类获取题目列表
     *
     * @param categoryId 分类ID
     * @return 题目列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Question>> getQuestionsByCategory(@PathVariable Long categoryId) {
        log.info("根据分类获取当前用户题目列表，分类ID: {}", categoryId);
        List<Question> questions = questionService.getQuestionsByCategory(categoryId);
        return Result.success("获取题目列表成功", questions);
    }

    /**
     * 根据题型获取题目列表
     *
     * @param type 题型
     * @return 题目列表
     */
    @GetMapping("/type/{type}")
    public Result<List<Question>> getQuestionsByType(@PathVariable String type) {
        log.info("根据题型获取当前用户题目列表，题型: {}", type);
        List<Question> questions = questionService.getQuestionsByType(type);
        return Result.success("获取题目列表成功", questions);
    }

    /**
     * 根据难度获取题目列表
     *
     * @param difficulty 难度
     * @return 题目列表
     */
    @GetMapping("/difficulty/{difficulty}")
    public Result<List<Question>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        log.info("根据难度获取当前用户题目列表，难度: {}", difficulty);
        List<Question> questions = questionService.getQuestionsByDifficulty(difficulty);
        return Result.success("获取题目列表成功", questions);
    }

    /**
     * 创建题目
     *
     * @param questionRequest 题目请求
     * @return 题目信息
     */
    @PostMapping
    public Result<Question> createQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        log.info("创建当前用户题目: {}", questionRequest.getTitle());
        Question question = questionService.createQuestion(questionRequest);
        return Result.success("创建题目成功", question);
    }

    /**
     * 更新题目
     *
     * @param id 题目ID
     * @param questionRequest 题目请求
     * @return 题目信息
     */
    @PutMapping("/{id}")
    public Result<Question> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest questionRequest) {
        log.info("更新当前用户题目，ID: {}", id);
        Question question = questionService.updateQuestion(id, questionRequest);
        return Result.success("更新题目成功", question);
    }

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteQuestion(@PathVariable Long id) {
        log.info("删除当前用户题目，ID: {}", id);
        questionService.deleteQuestion(id);
        return Result.success("删除题目成功");
    }
}
