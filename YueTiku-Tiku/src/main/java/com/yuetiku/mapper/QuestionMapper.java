package com.yuetiku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuetiku.dto.QuestionDetailWithJoinsDto;
import com.yuetiku.dto.QuizQuestionWithJoinsDto;
import com.yuetiku.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目Mapper接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    
    /**
     * 获取题目详情（包含分类、选项、答案信息）
     * 使用LEFT JOIN一次性查询所有相关数据
     *
     * @param id 题目ID
     * @return 题目详情列表
     */
    @Select("SELECT " +
            "q.id, q.category_id, q.type, q.title, q.content, q.explanation, " +
            "q.difficulty, q.points, q.source, q.tags, q.status, q.created_by, " +
            "q.created_at, q.updated_at, " +
            "c.name as category_name, " +
            "qo.id as option_id, qo.option_key, qo.option_content, qo.is_correct, " +
            "qo.sort_order, qo.created_at as option_created_at, " +
            "qa.id as answer_id, qa.answer_type, qa.correct_answer, " +
            "qa.answer_explanation, qa.created_at as answer_created_at " +
            "FROM questions q " +
            "LEFT JOIN categories c ON q.category_id = c.id " +
            "LEFT JOIN question_options qo ON q.id = qo.question_id " +
            "LEFT JOIN question_answers qa ON q.id = qa.question_id " +
            "WHERE q.id = #{id} " +
            "ORDER BY qo.sort_order ASC, qo.id ASC")
    List<QuestionDetailWithJoinsDto> getQuestionDetailWithJoins(@Param("id") Long id);
    
    /**
     * 随机获取题目（包含分类、选项信息）
     * 使用LEFT JOIN一次性查询所有相关数据，使用RAND()实现随机
     *
     * @param userId 用户ID
     * @param categoryId 分类ID（可选）
     * @param difficulty 难度（可选）
     * @param count 题目数量
     * @return 题目详情列表
     */
    @Select("SELECT " +
            "q.id, q.category_id, q.type, q.title, q.content, q.explanation, " +
            "q.difficulty, q.points, q.source, q.tags, q.status, q.created_by, " +
            "q.created_at, q.updated_at, " +
            "c.name as category_name, " +
            "qo.id as option_id, qo.option_key, qo.option_content, qo.is_correct, " +
            "qo.sort_order, qo.created_at as option_created_at " +
            "FROM (" +
            "  SELECT * FROM questions " +
            "  WHERE status = 1 AND created_by = #{userId} " +
            "  AND (#{categoryId} IS NULL OR category_id = #{categoryId}) " +
            "  AND (#{difficulty} IS NULL OR difficulty = #{difficulty}) " +
            "  ORDER BY RAND() " +
            "  LIMIT #{count}" +
            ") q " +
            "LEFT JOIN categories c ON q.category_id = c.id " +
            "LEFT JOIN question_options qo ON q.id = qo.question_id " +
            "ORDER BY q.id, qo.sort_order")
    List<QuizQuestionWithJoinsDto> getRandomQuestionsWithJoins(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("difficulty") String difficulty,
            @Param("count") Integer count
    );
}

