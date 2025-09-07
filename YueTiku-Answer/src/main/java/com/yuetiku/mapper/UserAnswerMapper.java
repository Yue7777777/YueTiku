package com.yuetiku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuetiku.entity.UserAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户答题记录Mapper
 */
@Mapper
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    /**
     * 获取用户答题历史（按分类）
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 答题历史列表
     */
    List<UserAnswer> getUserAnswerHistoryByCategory(@Param("userId") Long userId, 
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("offset") Integer offset, 
                                                   @Param("limit") Integer limit);

    /**
     * 获取用户答题历史（全部分类）
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 答题历史列表
     */
    List<UserAnswer> getUserAnswerHistory(@Param("userId") Long userId, 
                                        @Param("offset") Integer offset, 
                                        @Param("limit") Integer limit);

    /**
     * 统计用户答题数量（按分类）
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 答题数量
     */
    Integer countUserAnswersByCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * 统计用户答题数量（全部分类）
     *
     * @param userId 用户ID
     * @return 答题数量
     */
    Integer countUserAnswers(@Param("userId") Long userId);

    /**
     * 获取用户最近答题记录
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近答题记录
     */
    List<UserAnswer> getRecentAnswers(@Param("userId") Long userId, @Param("limit") Integer limit);
}