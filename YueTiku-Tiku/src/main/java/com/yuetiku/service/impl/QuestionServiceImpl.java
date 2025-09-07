package com.yuetiku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuetiku.context.BaseContext;
import com.yuetiku.dto.QuestionDetailResponse;
import com.yuetiku.dto.QuestionRequest;
import com.yuetiku.entity.*;
import com.yuetiku.mapper.*;
import com.yuetiku.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final CategoryMapper categoryMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Question createQuestion(QuestionRequest questionRequest) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查分类是否存在且属于当前用户
        LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(Category::getId, questionRequest.getCategoryId())
                .eq(Category::getUserId, currentUserId);
        Category category = categoryMapper.selectOne(categoryQueryWrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 创建题目
        Question question = new Question();
        BeanUtils.copyProperties(questionRequest, question);
        question.setCreatedBy(currentUserId);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        boolean saved = save(question);
        if (!saved) {
            throw new RuntimeException("创建题目失败");
        }

        // 创建题目选项
        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            for (QuestionRequest.QuestionOptionRequest optionRequest : questionRequest.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionKey(optionRequest.getOptionKey());
                option.setOptionContent(optionRequest.getOptionContent());
                option.setIsCorrect(optionRequest.getIsCorrect());
                option.setSortOrder(optionRequest.getSortOrder());
                option.setCreatedAt(LocalDateTime.now());
                questionOptionMapper.insert(option);
            }
        }

        // 创建题目答案
        if (questionRequest.getAnswer() != null) {
            QuestionAnswer answer = new QuestionAnswer();
            answer.setQuestionId(question.getId());
            answer.setAnswerType(questionRequest.getAnswer().getAnswerType());
            answer.setCorrectAnswer(questionRequest.getAnswer().getCorrectAnswer());
            answer.setAnswerExplanation(questionRequest.getAnswer().getAnswerExplanation());
            answer.setCreatedAt(LocalDateTime.now());
            questionAnswerMapper.insert(answer);
        }

        log.info("创建题目成功: {}, 用户ID: {}", question.getTitle(), currentUserId);
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Question updateQuestion(Long id, QuestionRequest questionRequest) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查题目是否存在且属于当前用户
        LambdaQueryWrapper<Question> questionQueryWrapper = new LambdaQueryWrapper<>();
        questionQueryWrapper.eq(Question::getId, id)
                .eq(Question::getCreatedBy, currentUserId);
        Question question = getOne(questionQueryWrapper);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 检查分类是否存在且属于当前用户
        LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(Category::getId, questionRequest.getCategoryId())
                .eq(Category::getUserId, currentUserId);
        Category category = categoryMapper.selectOne(categoryQueryWrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 更新题目
        BeanUtils.copyProperties(questionRequest, question);
        question.setId(id);
        question.setCreatedBy(currentUserId);
        question.setUpdatedAt(LocalDateTime.now());

        boolean updated = updateById(question);
        if (!updated) {
            throw new RuntimeException("更新题目失败");
        }

        // 删除原有选项
        LambdaQueryWrapper<QuestionOption> optionQueryWrapper = new LambdaQueryWrapper<>();
        optionQueryWrapper.eq(QuestionOption::getQuestionId, id);
        questionOptionMapper.delete(optionQueryWrapper);

        // 创建新选项
        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            for (QuestionRequest.QuestionOptionRequest optionRequest : questionRequest.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionKey(optionRequest.getOptionKey());
                option.setOptionContent(optionRequest.getOptionContent());
                option.setIsCorrect(optionRequest.getIsCorrect());
                option.setSortOrder(optionRequest.getSortOrder());
                option.setCreatedAt(LocalDateTime.now());
                questionOptionMapper.insert(option);
            }
        }

        // 删除原有答案
        LambdaQueryWrapper<QuestionAnswer> answerQueryWrapper = new LambdaQueryWrapper<>();
        answerQueryWrapper.eq(QuestionAnswer::getQuestionId, id);
        questionAnswerMapper.delete(answerQueryWrapper);

        // 创建新答案
        if (questionRequest.getAnswer() != null) {
            QuestionAnswer answer = new QuestionAnswer();
            answer.setQuestionId(question.getId());
            answer.setAnswerType(questionRequest.getAnswer().getAnswerType());
            answer.setCorrectAnswer(questionRequest.getAnswer().getCorrectAnswer());
            answer.setAnswerExplanation(questionRequest.getAnswer().getAnswerExplanation());
            answer.setCreatedAt(LocalDateTime.now());
            questionAnswerMapper.insert(answer);
        }

        log.info("更新题目成功: {}, 用户ID: {}", question.getTitle(), currentUserId);
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(Long id) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查题目是否存在且属于当前用户
        LambdaQueryWrapper<Question> questionQueryWrapper = new LambdaQueryWrapper<>();
        questionQueryWrapper.eq(Question::getId, id)
                .eq(Question::getCreatedBy, currentUserId);
        Question question = getOne(questionQueryWrapper);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 删除题目选项
        LambdaQueryWrapper<QuestionOption> optionQueryWrapper = new LambdaQueryWrapper<>();
        optionQueryWrapper.eq(QuestionOption::getQuestionId, id);
        questionOptionMapper.delete(optionQueryWrapper);

        // 删除题目答案
        LambdaQueryWrapper<QuestionAnswer> answerQueryWrapper = new LambdaQueryWrapper<>();
        answerQueryWrapper.eq(QuestionAnswer::getQuestionId, id);
        questionAnswerMapper.delete(answerQueryWrapper);

        // 删除题目
        boolean deleted = removeById(id);
        if (!deleted) {
            throw new RuntimeException("删除题目失败");
        }

        log.info("删除题目成功: {}, 用户ID: {}", question.getTitle(), currentUserId);
        return true;
    }

    @Override
    public QuestionDetailResponse getQuestionDetail(Long id) {
        Question question = getById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 获取分类信息
        Category category = categoryMapper.selectById(question.getCategoryId());

        // 构建响应对象
        QuestionDetailResponse response = new QuestionDetailResponse();
        BeanUtils.copyProperties(question, response);
        if (category != null) {
            response.setCategoryName(category.getName());
        }

        // 获取题目选项
        LambdaQueryWrapper<QuestionOption> optionQueryWrapper = new LambdaQueryWrapper<>();
        optionQueryWrapper.eq(QuestionOption::getQuestionId, id)
                .orderByAsc(QuestionOption::getSortOrder, QuestionOption::getId);
        List<QuestionOption> options = questionOptionMapper.selectList(optionQueryWrapper);
        response.setOptions(options.stream().map(option -> {
            QuestionDetailResponse.QuestionOptionResponse optionResponse = new QuestionDetailResponse.QuestionOptionResponse();
            BeanUtils.copyProperties(option, optionResponse);
            return optionResponse;
        }).collect(Collectors.toList()));

        // 获取题目答案
        LambdaQueryWrapper<QuestionAnswer> answerQueryWrapper = new LambdaQueryWrapper<>();
        answerQueryWrapper.eq(QuestionAnswer::getQuestionId, id);
        QuestionAnswer answer = questionAnswerMapper.selectOne(answerQueryWrapper);
        if (answer != null) {
            QuestionDetailResponse.QuestionAnswerResponse answerResponse = new QuestionDetailResponse.QuestionAnswerResponse();
            BeanUtils.copyProperties(answer, answerResponse);
            response.setAnswer(answerResponse);
        }

        return response;
    }

    @Override
    public List<Question> getQuestionsByCategory(Long categoryId) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 验证分类是否属于当前用户
        LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(Category::getId, categoryId)
                .eq(Category::getUserId, currentUserId);
        Category category = categoryMapper.selectOne(categoryQueryWrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getCategoryId, categoryId)
                .eq(Question::getCreatedBy, currentUserId)
                .orderByDesc(Question::getCreatedAt);
        return list(queryWrapper);
    }

    @Override
    public List<Question> getQuestionsByType(String type) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getType, type)
                .eq(Question::getCreatedBy, currentUserId)
                .orderByDesc(Question::getCreatedAt);
        return list(queryWrapper);
    }

    @Override
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getDifficulty, difficulty)
                .eq(Question::getCreatedBy, currentUserId)
                .orderByDesc(Question::getCreatedAt);
        return list(queryWrapper);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Question> getCurrentUserQuestions(Integer page, Integer size, Long categoryId, String type, String difficulty) {
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Question> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getCreatedBy, currentUserId);

        // 添加可选条件
        if (categoryId != null) {
            // 验证分类是否属于当前用户
            LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
            categoryQueryWrapper.eq(Category::getId, categoryId)
                    .eq(Category::getUserId, currentUserId);
            Category category = categoryMapper.selectOne(categoryQueryWrapper);
            if (category != null) {
                queryWrapper.eq(Question::getCategoryId, categoryId);
            }
        }
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(Question::getType, type);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            queryWrapper.eq(Question::getDifficulty, difficulty);
        }

        queryWrapper.orderByDesc(Question::getCreatedAt);
        return page(pageParam, queryWrapper);
    }
}
