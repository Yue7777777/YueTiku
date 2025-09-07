package com.yuetiku.service;

import com.yuetiku.dto.StatisticsOverviewResponse;
import com.yuetiku.dto.StatisticsCategoryResponse;
import com.yuetiku.dto.StatisticsTimelineResponse;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取统计概览
     *
     * @return 统计概览信息
     */
    StatisticsOverviewResponse getOverview();

    /**
     * 按分类统计
     *
     * @return 分类统计信息
     */
    List<StatisticsCategoryResponse> getCategoryStatistics();

    /**
     * 时间线统计
     *
     * @param days 统计天数
     * @return 时间线统计信息
     */
    List<StatisticsTimelineResponse> getTimelineStatistics(Integer days);

}
