package com.yuetiku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuetiku.context.BaseContext;
import com.yuetiku.dto.QuizAnswerRequest;
import com.yuetiku.dto.QuizAnswerResponse;
import com.yuetiku.dto.QuizHistoryResponse;
import com.yuetiku.dto.QuizQuestionResponse;
import com.yuetiku.dto.QuizQuestionsResponse;
import com.yuetiku.dto.QuizQuestionWithJoinsDto;
import com.yuetiku.entity.Category;
import com.yuetiku.entity.Question;
import com.yuetiku.entity.QuestionAnswer;
import com.yuetiku.entity.QuestionOption;
import com.yuetiku.entity.UserAnswer;
import com.yuetiku.entity.UserStatistics;
import com.yuetiku.mapper.CategoryMapper;
import com.yuetiku.mapper.QuestionAnswerMapper;
import com.yuetiku.mapper.QuestionMapper;
import com.yuetiku.mapper.QuestionOptionMapper;
import com.yuetiku.mapper.UserAnswerMapper;
import com.yuetiku.mapper.UserStatisticsMapper;
import com.yuetiku.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 答题服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final CategoryMapper categoryMapper;
    private final UserAnswerMapper userAnswerMapper;
    private final UserStatisticsMapper userStatisticsMapper;

    @Override
    public QuizQuestionsResponse getRandomQuestion(Long categoryId, String difficulty, Integer count) {
        log.info("获取随机题目，分类ID: {}, 难度: {}, 数量: {}", categoryId, difficulty, count);
        
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 使用JOIN查询一次性获取所有相关数据
        List<QuizQuestionWithJoinsDto> joinResults = questionMapper.getRandomQuestionsWithJoins(
                currentUserId, categoryId, difficulty, count);
        
        if (joinResults.isEmpty()) {
            throw new RuntimeException("没有找到符合条件的题目");
        }
        
        // 构建响应
        return buildQuizQuestionsResponseFromJoins(joinResults);
    }

    @Override
    public QuizQuestionsResponse getQuestionByCategory(Long categoryId, String difficulty, Integer count) {
        log.info("按分类获取题目，分类ID: {}, 难度: {}, 数量: {}", categoryId, difficulty, count);
        
        // 获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 使用JOIN查询一次性获取所有相关数据
        List<QuizQuestionWithJoinsDto> joinResults = questionMapper.getRandomQuestionsWithJoins(
                currentUserId, categoryId, difficulty, count);
        
        if (joinResults.isEmpty()) {
            throw new RuntimeException("该分类下没有找到符合条件的题目");
        }
        
        // 构建响应
        return buildQuizQuestionsResponseFromJoins(joinResults);
    }

    @Override
    @Transactional
    public QuizAnswerResponse submitAnswer(QuizAnswerRequest answerRequest) {
        log.info("提交答案，题目ID: {}, 用户答案: {}", answerRequest.getQuestionId(), answerRequest.getUserAnswer());
        
        Long userId = BaseContext.getCurrentId();
        
        // 获取题目信息
        Question question = questionMapper.selectById(answerRequest.getQuestionId());
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        
        // 获取正确答案
        LambdaQueryWrapper<QuestionAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(QuestionAnswer::getQuestionId, answerRequest.getQuestionId());
        QuestionAnswer correctAnswer = questionAnswerMapper.selectOne(answerWrapper);
        
        if (correctAnswer == null) {
            throw new RuntimeException("题目答案不存在");
        }
        
        // 验证答案
        boolean isCorrect = validateAnswer(answerRequest.getUserAnswer(), correctAnswer, question.getType());
        BigDecimal score = isCorrect ? BigDecimal.valueOf(question.getPoints()) : BigDecimal.ZERO;
        
        // 保存答题记录
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setQuestionId(answerRequest.getQuestionId());
        userAnswer.setUserAnswer(answerRequest.getUserAnswer());
        userAnswer.setIsCorrect(isCorrect);
        userAnswer.setScore(score);
        userAnswer.setAnswerTime(LocalDateTime.now());
        userAnswerMapper.insert(userAnswer);
        
        // 更新用户统计
        log.info("--------获取到的分类id为："+question.getCategoryId());
        updateUserStatistics(userId, question.getCategoryId(), isCorrect, score);
        
        // 构建响应
        QuizAnswerResponse response = new QuizAnswerResponse();
        response.setQuestionId(answerRequest.getQuestionId());
        response.setUserAnswer(answerRequest.getUserAnswer());
        response.setCorrectAnswer(correctAnswer.getCorrectAnswer());
        response.setIsCorrect(isCorrect);
        response.setScore(score);
        response.setExplanation(question.getExplanation());
        response.setAnswerTime(userAnswer.getAnswerTime());
        
        return response;
    }

    @Override
    public List<QuizHistoryResponse> getQuizHistory(Integer page, Integer size, Long categoryId) {
        log.info("获取答题历史，页码: {}, 每页大小: {}, 分类ID: {}", page, size, categoryId);
        
        Long userId = BaseContext.getCurrentId();
        int offset = (page - 1) * size;
        
        // 获取答题历史
        List<UserAnswer> userAnswers;
        if (categoryId != null) {
            userAnswers = userAnswerMapper.getUserAnswerHistoryByCategory(userId, categoryId, offset, size);
        } else {
            userAnswers = userAnswerMapper.getUserAnswerHistory(userId, offset, size);
        }
        
        // 构建响应
        List<QuizHistoryResponse> historyList = new ArrayList<>();
        for (UserAnswer userAnswer : userAnswers) {
            QuizHistoryResponse history = new QuizHistoryResponse();
            history.setId(userAnswer.getId());
            history.setQuestionId(userAnswer.getQuestionId());
            history.setUserAnswer(userAnswer.getUserAnswer());
            history.setIsCorrect(userAnswer.getIsCorrect());
            history.setScore(userAnswer.getScore());
            history.setAnswerTime(userAnswer.getAnswerTime());
            
            // 获取题目信息
            Question question = questionMapper.selectById(userAnswer.getQuestionId());
            if (question != null) {
                history.setQuestionTitle(question.getTitle());
                history.setType(question.getType());
                history.setDifficulty(question.getDifficulty());
                
                // 获取分类信息
                Category category = categoryMapper.selectById(question.getCategoryId());
                if (category != null) {
                    history.setCategoryName(category.getName());
                }
            }
            
            historyList.add(history);
        }
        
        return historyList;
    }

    @Override
    public Object getUserStatistics(Long categoryId) {
        log.info("获取用户统计信息，分类ID: {}", categoryId);
        
        Long userId = BaseContext.getCurrentId();
        
        if (categoryId == null) {
            // 获取全部分类统计
            List<UserStatistics> statistics = userStatisticsMapper.getUserStatistics(userId);
            return statistics;
        } else {
            // 获取指定分类统计
            List<UserStatistics> statistics = userStatisticsMapper.getUserStatisticsByCategory(userId, categoryId);
            return statistics.isEmpty() ? null : statistics.get(0);
        }
    }

    /**
     * 构建答题题目列表响应
     */
    private QuizQuestionsResponse buildQuizQuestionsResponse(List<Question> questions) {
        if (questions.isEmpty()) {
            return null;
        }
        
        QuizQuestionsResponse response = new QuizQuestionsResponse();
        response.setCount(questions.size());
        
        // 获取第一道题目的分类信息
        Question firstQuestion = questions.get(0);
        response.setCategoryId(firstQuestion.getCategoryId());
        
        // 获取分类名称
        Category category = categoryMapper.selectById(firstQuestion.getCategoryId());
        if (category != null) {
            response.setCategoryName(category.getName());
        }
        
        // 构建每道题目的响应
        List<QuizQuestionResponse> questionResponses = new ArrayList<>();
        for (Question question : questions) {
            QuizQuestionResponse questionResponse = buildSingleQuestionResponse(question);
            questionResponses.add(questionResponse);
        }
        
        response.setQuestions(questionResponses);
        
        return response;
    }
    
    /**
     * 从JOIN查询结果构建答题题目列表响应
     */
    private QuizQuestionsResponse buildQuizQuestionsResponseFromJoins(List<QuizQuestionWithJoinsDto> joinResults) {
        if (joinResults.isEmpty()) {
            return null;
        }
        
        // 按题目ID分组，因为JOIN查询会产生多行（每个选项一行）
        Map<Long, List<QuizQuestionWithJoinsDto>> groupedResults = joinResults.stream()
                .collect(Collectors.groupingBy(QuizQuestionWithJoinsDto::getId));
        
        QuizQuestionsResponse response = new QuizQuestionsResponse();
        response.setCount(groupedResults.size());
        
        // 获取第一道题目的分类信息
        QuizQuestionWithJoinsDto firstRecord = joinResults.get(0);
        response.setCategoryId(firstRecord.getCategoryId());
        response.setCategoryName(firstRecord.getCategoryName());
        
        // 构建每道题目的响应
        List<QuizQuestionResponse> questionResponses = new ArrayList<>();
        for (Map.Entry<Long, List<QuizQuestionWithJoinsDto>> entry : groupedResults.entrySet()) {
            List<QuizQuestionWithJoinsDto> questionRecords = entry.getValue();
            QuizQuestionResponse questionResponse = buildSingleQuestionResponseFromJoins(questionRecords);
            questionResponses.add(questionResponse);
        }
        
        response.setQuestions(questionResponses);
        
        return response;
    }
    
    /**
     * 从JOIN查询结果构建单道题目响应
     */
    private QuizQuestionResponse buildSingleQuestionResponseFromJoins(List<QuizQuestionWithJoinsDto> questionRecords) {
        if (questionRecords.isEmpty()) {
            return null;
        }
        
        // 取第一条记录作为题目基本信息
        QuizQuestionWithJoinsDto firstRecord = questionRecords.get(0);
        
        QuizQuestionResponse response = new QuizQuestionResponse();
        response.setId(firstRecord.getId());
        response.setCategoryId(firstRecord.getCategoryId());
        response.setCategoryName(firstRecord.getCategoryName());
        response.setType(firstRecord.getType());
        response.setTitle(firstRecord.getTitle());
        response.setContent(firstRecord.getContent());
        response.setDifficulty(firstRecord.getDifficulty());
        response.setPoints(firstRecord.getPoints());
        response.setSource(firstRecord.getSource());
        response.setTags(firstRecord.getTags());
        response.setCount(1);
        
        // 构建选项列表
        if ("single".equals(firstRecord.getType()) || "multiple".equals(firstRecord.getType())) {
            // 选择题：从JOIN结果中获取选项
            List<QuizQuestionResponse.QuizOptionResponse> optionResponses = questionRecords.stream()
                    .filter(record -> record.getOptionId() != null) // 过滤掉没有选项的记录
                    .map(record -> {
                        QuizQuestionResponse.QuizOptionResponse optionResponse = new QuizQuestionResponse.QuizOptionResponse();
                        optionResponse.setId(record.getOptionId());
                        optionResponse.setOptionKey(record.getOptionKey());
                        optionResponse.setOptionContent(record.getOptionContent());
                        optionResponse.setSortOrder(record.getSortOrder());
                        return optionResponse;
                    })
                    .distinct() // 去重，因为JOIN可能产生重复记录
                    .sorted(Comparator.comparing(QuizQuestionResponse.QuizOptionResponse::getSortOrder)) // 按排序字段排序
                    .collect(Collectors.toList());
            response.setOptions(optionResponses);
        } else if ("judge".equals(firstRecord.getType())) {
            // 判断题：创建固定的选项
            List<QuizQuestionResponse.QuizOptionResponse> optionResponses = new ArrayList<>();
            
            QuizQuestionResponse.QuizOptionResponse trueOption = new QuizQuestionResponse.QuizOptionResponse();
            trueOption.setId(1L);
            trueOption.setOptionKey("A");
            trueOption.setOptionContent("正确");
            trueOption.setSortOrder(1);
            optionResponses.add(trueOption);
            
            QuizQuestionResponse.QuizOptionResponse falseOption = new QuizQuestionResponse.QuizOptionResponse();
            falseOption.setId(2L);
            falseOption.setOptionKey("B");
            falseOption.setOptionContent("错误");
            falseOption.setSortOrder(2);
            optionResponses.add(falseOption);
            
            response.setOptions(optionResponses);
        }
        
        return response;
    }
    
    /**
     * 构建单道题目响应
     */
    private QuizQuestionResponse buildSingleQuestionResponse(Question question) {
        QuizQuestionResponse response = new QuizQuestionResponse();
        response.setId(question.getId());
        response.setCategoryId(question.getCategoryId());
        response.setType(question.getType());
        response.setTitle(question.getTitle());
        response.setContent(question.getContent());
        response.setDifficulty(question.getDifficulty());
        response.setPoints(question.getPoints());
        response.setSource(question.getSource());
        response.setTags(question.getTags());
        response.setCount(1);
        
        // 获取分类名称
        Category category = categoryMapper.selectById(question.getCategoryId());
        if (category != null) {
            response.setCategoryName(category.getName());
        }
        
        // 如果是选择题，获取选项
        if ("single".equals(question.getType()) || "multiple".equals(question.getType()) || "judge".equals(question.getType())) {
            LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();
            optionWrapper.eq(QuestionOption::getQuestionId, question.getId())
                        .orderByAsc(QuestionOption::getSortOrder);
            List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);
            
            List<QuizQuestionResponse.QuizOptionResponse> optionResponses = new ArrayList<>();
            for (QuestionOption option : options) {
                QuizQuestionResponse.QuizOptionResponse optionResponse = new QuizQuestionResponse.QuizOptionResponse();
                optionResponse.setId(option.getId());
                optionResponse.setOptionKey(option.getOptionKey());
                optionResponse.setOptionContent(option.getOptionContent());
                optionResponse.setSortOrder(option.getSortOrder());
                optionResponses.add(optionResponse);
            }
            response.setOptions(optionResponses);
        }
        
        return response;
    }

    /**
     * 验证答案
     */
    private boolean validateAnswer(String userAnswer, QuestionAnswer correctAnswer, String questionType) {
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }
        
        String correctAnswerStr = correctAnswer.getCorrectAnswer();
        
        switch (questionType) {
            case "single":
            case "judge":
                return userAnswer.trim().equalsIgnoreCase(correctAnswerStr.trim());
            case "multiple":
                // 多选题需要比较选项集合
                return compareMultipleChoice(userAnswer, correctAnswerStr);
            case "fill":
            case "answer":
                // 填空题和简答题进行模糊匹配
                return userAnswer.trim().toLowerCase().contains(correctAnswerStr.trim().toLowerCase()) ||
                       correctAnswerStr.trim().toLowerCase().contains(userAnswer.trim().toLowerCase());
            default:
                return false;
        }
    }

    /**
     * 比较多选题答案
     */
    private boolean compareMultipleChoice(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }
        
        // 将答案字符串转换为字符集合进行比较
        String userAnswerSorted = userAnswer.trim().toUpperCase().chars()
                .sorted()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        
        String correctAnswerSorted = correctAnswer.trim().toUpperCase().chars()
                .sorted()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        
        return userAnswerSorted.equals(correctAnswerSorted);
    }

    /**
     * 更新用户统计
     */
    @Transactional
    public void updateUserStatistics(Long userId, Long categoryId, boolean isCorrect, BigDecimal score) {
        // 更新全部分类统计
        updateCategoryStatistics(userId, null, isCorrect, score);
        
        // 更新指定分类统计
        if (categoryId != null) {
            updateCategoryStatistics(userId, categoryId, isCorrect, score);
        }
    }

    /**
     * 更新分类统计
     */
    private void updateCategoryStatistics(Long userId, Long categoryId, boolean isCorrect, BigDecimal score) {
        LambdaQueryWrapper<UserStatistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserStatistics::getUserId, userId);
        
        if (categoryId == null) {
            queryWrapper.isNull(UserStatistics::getCategoryId);
        } else {
            queryWrapper.eq(UserStatistics::getCategoryId, categoryId);
        }
        
        UserStatistics statistics = userStatisticsMapper.selectOne(queryWrapper);
        
        if (statistics == null) {
            // 创建新的统计记录
            statistics = new UserStatistics();
            statistics.setUserId(userId);
            statistics.setCategoryId(categoryId);
            statistics.setTotalQuestions(1);
            statistics.setCorrectAnswers(isCorrect ? 1 : 0);
            statistics.setTotalScore(score);
            statistics.setAccuracyRate(isCorrect ? BigDecimal.valueOf(100) : BigDecimal.ZERO);
            statistics.setLastAnswerTime(LocalDateTime.now());
            statistics.setCreatedAt(LocalDateTime.now());
            statistics.setUpdatedAt(LocalDateTime.now());
            userStatisticsMapper.insert(statistics);
        } else {
            // 更新现有统计记录
            statistics.setTotalQuestions(statistics.getTotalQuestions() + 1);
            if (isCorrect) {
                statistics.setCorrectAnswers(statistics.getCorrectAnswers() + 1);
            }
            statistics.setTotalScore(statistics.getTotalScore().add(score));
            statistics.setAccuracyRate(statistics.getTotalQuestions() > 0 ? 
                BigDecimal.valueOf(statistics.getCorrectAnswers()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(statistics.getTotalQuestions()), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO);
            statistics.setLastAnswerTime(LocalDateTime.now());
            statistics.setUpdatedAt(LocalDateTime.now());
            userStatisticsMapper.updateById(statistics);
        }
    }
}