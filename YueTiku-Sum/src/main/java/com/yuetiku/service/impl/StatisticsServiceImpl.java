package com.yuetiku.service.impl;

import com.yuetiku.context.BaseContext;
import com.yuetiku.dto.*;
import com.yuetiku.entity.StatisticsOverview;
import com.yuetiku.mapper.StatisticsMapper;
import com.yuetiku.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    public StatisticsOverviewResponse getOverview() {
        log.info("获取统计概览");
        
        Long userId = BaseContext.getCurrentId();
        
        // 获取用户统计概览
        StatisticsOverview overview = statisticsMapper.getUserOverview(userId);
        
        // 获取额外统计信息
        Integer todayQuestions = statisticsMapper.getTodayQuestions(userId);
        Integer weekQuestions = statisticsMapper.getWeekQuestions(userId);
        Integer monthQuestions = statisticsMapper.getMonthQuestions(userId);
        Integer consecutiveDays = statisticsMapper.getConsecutiveDays(userId);
        
        // 构建响应
        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        
        if (overview != null) {
            response.setTotalQuestions(overview.getTotalQuestions());
            response.setCorrectAnswers(overview.getCorrectAnswers());
            response.setTotalScore(overview.getTotalScore());
            response.setAccuracyRate(overview.getAccuracyRate());
            response.setLastAnswerTime(overview.getLastAnswerTime());
            
            // 计算平均分
            if (overview.getTotalQuestions() != null && overview.getTotalQuestions() > 0) {
                response.setAverageScore(overview.getTotalScore().divide(BigDecimal.valueOf(overview.getTotalQuestions()), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                response.setAverageScore(BigDecimal.ZERO);
            }
        } else {
            // 如果没有统计记录，设置默认值
            response.setTotalQuestions(0);
            response.setCorrectAnswers(0);
            response.setTotalScore(BigDecimal.ZERO);
            response.setAccuracyRate(BigDecimal.ZERO);
            response.setAverageScore(BigDecimal.ZERO);
        }
        
        response.setTodayQuestions(todayQuestions != null ? todayQuestions : 0);
        response.setWeekQuestions(weekQuestions != null ? weekQuestions : 0);
        response.setMonthQuestions(monthQuestions != null ? monthQuestions : 0);
        response.setConsecutiveDays(consecutiveDays != null ? consecutiveDays : 0);
        
        // 计算平均每日答题数（基于总答题数和连续天数）
        if (response.getConsecutiveDays() > 0) {
            response.setAverageDailyQuestions(BigDecimal.valueOf(response.getTotalQuestions()).divide(BigDecimal.valueOf(response.getConsecutiveDays()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            response.setAverageDailyQuestions(BigDecimal.ZERO);
        }
        
        // 学习总时长（假设每题平均2分钟）
        response.setTotalStudyMinutes(response.getTotalQuestions() * 2);
        
        return response;
    }

    @Override
    public List<StatisticsCategoryResponse> getCategoryStatistics() {
        log.info("获取分类统计");
        
        Long userId = BaseContext.getCurrentId();
        
        // 获取用户分类统计
        List<StatisticsOverview> categoryStats = statisticsMapper.getUserCategoryStatistics(userId);
        
        // 构建响应
        List<StatisticsCategoryResponse> responseList = new ArrayList<>();
        int ranking = 1;
        
        for (StatisticsOverview stat : categoryStats) {
            StatisticsCategoryResponse response = new StatisticsCategoryResponse();
            response.setCategoryId(stat.getCategoryId());
            response.setCategoryName(stat.getCategoryName());
            response.setTotalQuestions(stat.getTotalQuestions());
            response.setCorrectAnswers(stat.getCorrectAnswers());
            response.setTotalScore(stat.getTotalScore());
            response.setAccuracyRate(stat.getAccuracyRate());
            response.setLastAnswerTime(stat.getLastAnswerTime());
            response.setRanking(ranking++);
            
            // 计算平均分
            if (stat.getTotalQuestions() != null && stat.getTotalQuestions() > 0) {
                response.setAverageScore(stat.getTotalScore().divide(BigDecimal.valueOf(stat.getTotalQuestions()), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                response.setAverageScore(BigDecimal.ZERO);
            }
            
            // 设置今日、本周、本月答题数（从数据库查询结果获取）
            response.setTodayQuestions(stat.getTodayQuestions() != null ? stat.getTodayQuestions() : 0);
            response.setWeekQuestions(stat.getWeekQuestions() != null ? stat.getWeekQuestions() : 0);
            response.setMonthQuestions(stat.getMonthQuestions() != null ? stat.getMonthQuestions() : 0);
            
            responseList.add(response);
        }
        
        return responseList;
    }

    @Override
    public List<StatisticsTimelineResponse> getTimelineStatistics(Integer days) {
        log.info("获取时间线统计，天数: {}", days);
        
        Long userId = BaseContext.getCurrentId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 获取时间线统计
        List<StatisticsOverview> timelineStats = statisticsMapper.getTimelineStatistics(userId, startDate, endDate);
        
        // 构建响应
        List<StatisticsTimelineResponse> responseList = new ArrayList<>();
        
        for (StatisticsOverview stat : timelineStats) {
            StatisticsTimelineResponse response = new StatisticsTimelineResponse();
            response.setDate(stat.getAnswerDate() != null ? stat.getAnswerDate() : stat.getLastAnswerTime().toLocalDate());
            response.setQuestionCount(stat.getTotalQuestions());
            response.setCorrectCount(stat.getCorrectAnswers());
            response.setScore(stat.getTotalScore());
            response.setAccuracyRate(stat.getAccuracyRate());
            response.setStudyMinutes(stat.getStudyMinutes() != null ? stat.getStudyMinutes() : stat.getTotalQuestions() * 2);
            response.setConsecutiveDays(stat.getConsecutiveDays() != null ? stat.getConsecutiveDays() : 0);
            
            responseList.add(response);
        }
        
        return responseList;
    }

}
