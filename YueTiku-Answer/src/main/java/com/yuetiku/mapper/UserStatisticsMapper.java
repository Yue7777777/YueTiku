package com.yuetiku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuetiku.entity.UserStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户统计Mapper
 */
@Mapper
public interface UserStatisticsMapper extends BaseMapper<UserStatistics> {

    /**
     * 获取用户统计信息（按分类）
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 统计信息
     */
    List<UserStatistics> getUserStatisticsByCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * 获取用户统计信息（全部分类）
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    List<UserStatistics> getUserStatistics(@Param("userId") Long userId);

    /**
     * 获取用户全部分类统计
     *
     * @param userId 用户ID
     * @return 全部分类统计
     */
    UserStatistics getOverallStatistics(@Param("userId") Long userId);

    /**
     * 获取用户分类统计列表
     *
     * @param userId 用户ID
     * @return 分类统计列表
     */
    List<UserStatistics> getCategoryStatistics(@Param("userId") Long userId);
}