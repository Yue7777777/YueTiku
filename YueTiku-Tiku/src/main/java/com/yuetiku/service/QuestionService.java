package com.yuetiku.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuetiku.dto.QuestionDetailResponse;
import com.yuetiku.dto.QuestionRequest;
import com.yuetiku.entity.Question;

import java.util.List;

/**
 * 题目服务接口
 */
public interface QuestionService extends IService<Question> {

    /**
     * 创建题目
     *
     * @param questionRequest 题目请求
     * @return 题目信息
     */
    Question createQuestion(QuestionRequest questionRequest);

    /**
     * 更新题目
     *
     * @param id 题目ID
     * @param questionRequest 题目请求
     * @return 题目信息
     */
    Question updateQuestion(Long id, QuestionRequest questionRequest);

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return 删除结果
     */
    boolean deleteQuestion(Long id);

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    QuestionDetailResponse getQuestionDetail(Long id);

    /**
     * 根据分类获取题目列表（当前用户）
     *
     * @param categoryId 分类ID
     * @return 题目列表
     */
    List<Question> getQuestionsByCategory(Long categoryId);

    /**
     * 根据题型获取题目列表（当前用户）
     *
     * @param type 题型
     * @return 题目列表
     */
    List<Question> getQuestionsByType(String type);

    /**
     * 根据难度获取题目列表（当前用户）
     *
     * @param difficulty 难度
     * @return 题目列表
     */
    List<Question> getQuestionsByDifficulty(String difficulty);

    /**
     * 获取当前用户的题目列表（分页）
     *
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @param type 题型（可选）
     * @param difficulty 难度（可选）
     * @return 题目列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<Question> getCurrentUserQuestions(Integer page, Integer size, Long categoryId, String type, String difficulty);
}
