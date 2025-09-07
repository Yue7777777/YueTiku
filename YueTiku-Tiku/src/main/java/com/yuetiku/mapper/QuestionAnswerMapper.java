package com.yuetiku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuetiku.entity.QuestionAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目答案Mapper接口
 */
@Mapper
public interface QuestionAnswerMapper extends BaseMapper<QuestionAnswer> {
}

