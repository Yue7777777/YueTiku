package com.yuetiku.service;

import com.yuetiku.dto.QuizAnswerRequest;
import com.yuetiku.dto.QuizAnswerResponse;
import com.yuetiku.dto.QuizHistoryResponse;
import com.yuetiku.dto.QuizQuestionResponse;
import com.yuetiku.dto.QuizQuestionsResponse;

import java.util.List;

/**
 * 答题服务接口
 */
public interface QuizService {

    /**
     * 获取随机题目
     *
     * @param categoryId 分类ID（可选）
     * @param difficulty 难度（可选）
     * @param count 题目数量
     * @return 题目信息
     */
    QuizQuestionsResponse getRandomQuestion(Long categoryId, String difficulty, Integer count);

    /**
     * 按分类获取题目
     *
     * @param categoryId 分类ID
     * @param difficulty 难度（可选）
     * @param count 题目数量
     * @return 题目信息
     */
    QuizQuestionsResponse getQuestionByCategory(Long categoryId, String difficulty, Integer count);

    /**
     * 提交答案
     *
     * @param answerRequest 答案请求
     * @return 答题结果
     */
    QuizAnswerResponse submitAnswer(QuizAnswerRequest answerRequest);

    /**
     * 获取答题历史
     *
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @return 答题历史列表
     */
    List<QuizHistoryResponse> getQuizHistory(Integer page, Integer size, Long categoryId);

    /**
     * 获取用户统计信息
     *
     * @param categoryId 分类ID（可选）
     * @return 统计信息
     */
    Object getUserStatistics(Long categoryId);
}
