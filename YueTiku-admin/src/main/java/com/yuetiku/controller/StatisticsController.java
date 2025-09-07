package com.yuetiku.controller;

import com.yuetiku.common.Result;
import com.yuetiku.dto.StatisticsOverviewResponse;
import com.yuetiku.dto.StatisticsCategoryResponse;
import com.yuetiku.dto.StatisticsTimelineResponse;
import com.yuetiku.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取统计概览
     *
     * @return 统计概览信息
     */
    @GetMapping("/overview")
    public Result<StatisticsOverviewResponse> getOverview() {
        log.info("获取统计概览");
        
        StatisticsOverviewResponse overview = statisticsService.getOverview();
        
        return Result.success("获取统计概览成功", overview);
    }

    /**
     * 按分类统计
     *
     * @return 分类统计信息
     */
    @GetMapping("/category")
    public Result<List<StatisticsCategoryResponse>> getCategoryStatistics() {
        log.info("获取分类统计");
        
        List<StatisticsCategoryResponse> categoryStats = statisticsService.getCategoryStatistics();
        
        return Result.success("获取分类统计成功", categoryStats);
    }

    /**
     * 时间线统计
     *
     * @param days 统计天数（默认30天）
     * @return 时间线统计信息
     */
    @GetMapping("/timeline")
    public Result<List<StatisticsTimelineResponse>> getTimelineStatistics(
            @RequestParam(defaultValue = "30") Integer days) {
        log.info("获取时间线统计，天数: {}", days);
        
        List<StatisticsTimelineResponse> timelineStats = statisticsService.getTimelineStatistics(days);
        
        return Result.success("获取时间线统计成功", timelineStats);
    }

}
