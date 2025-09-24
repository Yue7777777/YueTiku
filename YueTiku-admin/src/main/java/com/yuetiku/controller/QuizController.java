package com.yuetiku.controller;

import com.yuetiku.common.Result;
import com.yuetiku.dto.QuizAnswerRequest;
import com.yuetiku.dto.QuizAnswerResponse;
import com.yuetiku.dto.QuizHistoryResponse;
import com.yuetiku.dto.QuizQuestionResponse;
import com.yuetiku.dto.QuizQuestionsResponse;
import com.yuetiku.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 答题控制器
 */
@Slf4j
@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * 随机获取题目
     *
     * @param categoryId 分类ID（可选）
     * @param difficulty 难度（可选）
     * @param count 题目数量（默认20）
     * @return 题目信息
     */
    @GetMapping("/random")
    public Result<QuizQuestionsResponse> getRandomQuestion(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "20") Integer count) {
        log.info("随机获取题目，分类ID: {}, 难度: {}, 数量: {}", categoryId, difficulty, count);
        
        QuizQuestionsResponse questions = quizService.getRandomQuestion(categoryId, difficulty, count);
        
        return Result.success("获取随机题目成功", questions);
    }

    /**
     * 按分类获取题目
     *
     * @param categoryId 分类ID
     * @param difficulty 难度（可选）
     * @param count 题目数量（默认20）
     * @return 题目信息
     */
    @GetMapping("/category/{categoryId}")
    public Result<QuizQuestionsResponse> getQuestionByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "20") Integer count) {
        log.info("按分类获取题目，分类ID: {}, 难度: {}, 数量: {}", categoryId, difficulty, count);
        
        QuizQuestionsResponse questions = quizService.getQuestionByCategory(categoryId, difficulty, count);
        
        return Result.success("获取分类题目成功", questions);
    }

    /**
     * 提交答案
     *
     * @param answerRequest 答案请求
     * @return 答题结果
     */
    @PostMapping("/submit")
    public Result<QuizAnswerResponse> submitAnswer(@Valid @RequestBody QuizAnswerRequest answerRequest) {
        log.info("提交答案，题目ID: {}, 用户答案: {}", answerRequest.getQuestionId(), answerRequest.getUserAnswer());
        
        QuizAnswerResponse result = quizService.submitAnswer(answerRequest);
        
        return Result.success("提交答案成功", result);
    }

    /**
     * 获取答题历史
     *
     * @param page 页码（默认1）
     * @param size 每页大小（默认10）
     * @param categoryId 分类ID（可选）
     * @return 答题历史列表
     */
    @GetMapping("/history")
    public Result<List<QuizHistoryResponse>> getQuizHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId) {
        log.info("获取答题历史，页码: {}, 每页大小: {}, 分类ID: {}", page, size, categoryId);
        
        List<QuizHistoryResponse> history = quizService.getQuizHistory(page, size, categoryId);
        
        return Result.success("获取答题历史成功", history);
    }

    /**
     * 获取用户统计信息
     *
     * @param categoryId 分类ID（可选）
     * @return 用户统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getUserStatistics(@RequestParam(required = false) Long categoryId) {
        log.info("获取用户统计信息，分类ID: {}", categoryId);
        
        Object statistics = quizService.getUserStatistics(categoryId);
        
        return Result.success("获取统计信息成功", statistics);
    }
}
