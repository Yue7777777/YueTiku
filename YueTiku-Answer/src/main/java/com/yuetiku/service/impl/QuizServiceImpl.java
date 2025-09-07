package com.yuetiku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuetiku.context.BaseContext;
import com.yuetiku.dto.QuizAnswerRequest;
import com.yuetiku.dto.QuizAnswerResponse;
import com.yuetiku.dto.QuizHistoryResponse;
import com.yuetiku.dto.QuizQuestionResponse;
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
import java.util.List;
import java.util.Random;

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
    public QuizQuestionResponse getRandomQuestion(Long categoryId, String difficulty, Integer count) {
        log.info("获取随机题目，分类ID: {}, 难度: {}, 数量: {}", categoryId, difficulty, count);
        
        // 构建查询条件
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getStatus, 1); // 只获取正常状态的题目
        
        if (categoryId != null) {
            queryWrapper.eq(Question::getCategoryId, categoryId);
        }
        
        if (difficulty != null) {
            queryWrapper.eq(Question::getDifficulty, difficulty);
        }
        
        // 获取所有符合条件的题目
        List<Question> allQuestions = questionMapper.selectList(queryWrapper);
        
        if (allQuestions.isEmpty()) {
            throw new RuntimeException("没有找到符合条件的题目");
        }
        
        // 随机选择题目
        Random random = new Random();
        List<Question> selectedQuestions = new ArrayList<>();
        int actualCount = Math.min(count, allQuestions.size());
        
        for (int i = 0; i < actualCount; i++) {
            int randomIndex = random.nextInt(allQuestions.size());
            selectedQuestions.add(allQuestions.get(randomIndex));
            allQuestions.remove(randomIndex); // 避免重复选择
        }
        
        // 构建响应
        return buildQuizQuestionResponse(selectedQuestions);
    }

    @Override
    public QuizQuestionResponse getQuestionByCategory(Long categoryId, String difficulty, Integer count) {
        log.info("按分类获取题目，分类ID: {}, 难度: {}, 数量: {}", categoryId, difficulty, count);
        
        // 构建查询条件
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getCategoryId, categoryId)
                   .eq(Question::getStatus, 1);
        
        if (difficulty != null) {
            queryWrapper.eq(Question::getDifficulty, difficulty);
        }
        
        // 获取题目列表
        List<Question> questions = questionMapper.selectList(queryWrapper);
        
        if (questions.isEmpty()) {
            throw new RuntimeException("该分类下没有找到符合条件的题目");
        }
        
        // 随机选择指定数量的题目
        Random random = new Random();
        List<Question> selectedQuestions = new ArrayList<>();
        int actualCount = Math.min(count, questions.size());
        
        for (int i = 0; i < actualCount; i++) {
            int randomIndex = random.nextInt(questions.size());
            selectedQuestions.add(questions.get(randomIndex));
            questions.remove(randomIndex);
        }
        
        // 构建响应
        return buildQuizQuestionResponse(selectedQuestions);
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
     * 构建答题题目响应
     */
    private QuizQuestionResponse buildQuizQuestionResponse(List<Question> questions) {
        if (questions.isEmpty()) {
            return null;
        }
        
        Question firstQuestion = questions.get(0);
        
        QuizQuestionResponse response = new QuizQuestionResponse();
        response.setId(firstQuestion.getId());
        response.setCategoryId(firstQuestion.getCategoryId());
        response.setType(firstQuestion.getType());
        response.setTitle(firstQuestion.getTitle());
        response.setContent(firstQuestion.getContent());
        response.setDifficulty(firstQuestion.getDifficulty());
        response.setPoints(firstQuestion.getPoints());
        response.setSource(firstQuestion.getSource());
        response.setTags(firstQuestion.getTags());
        response.setCount(questions.size());
        
        // 获取分类名称
        Category category = categoryMapper.selectById(firstQuestion.getCategoryId());
        if (category != null) {
            response.setCategoryName(category.getName());
        }
        
        // 如果是选择题，获取选项
        if ("single".equals(firstQuestion.getType()) || "multiple".equals(firstQuestion.getType()) || "judge".equals(firstQuestion.getType())) {
            LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();
            optionWrapper.eq(QuestionOption::getQuestionId, firstQuestion.getId())
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