package com.yuetiku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuetiku.entity.StatisticsOverview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计Mapper
 */
@Mapper
public interface StatisticsMapper extends BaseMapper<StatisticsOverview> {

    /**
     * 获取用户统计概览
     *
     * @param userId 用户ID
     * @return 统计概览
     */
    StatisticsOverview getUserOverview(@Param("userId") Long userId);

    /**
     * 获取用户分类统计
     *
     * @param userId 用户ID
     * @return 分类统计列表
     */
    List<StatisticsOverview> getUserCategoryStatistics(@Param("userId") Long userId);

    /**
     * 获取时间线统计
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间线统计
     */
    List<StatisticsOverview> getTimelineStatistics(@Param("userId") Long userId, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);


    /**
     * 获取今日答题数
     *
     * @param userId 用户ID
     * @return 今日答题数
     */
    Integer getTodayQuestions(@Param("userId") Long userId);

    /**
     * 获取本周答题数
     *
     * @param userId 用户ID
     * @return 本周答题数
     */
    Integer getWeekQuestions(@Param("userId") Long userId);

    /**
     * 获取本月答题数
     *
     * @param userId 用户ID
     * @return 本月答题数
     */
    Integer getMonthQuestions(@Param("userId") Long userId);

    /**
     * 获取连续答题天数
     *
     * @param userId 用户ID
     * @return 连续答题天数
     */
    Integer getConsecutiveDays(@Param("userId") Long userId);
}
