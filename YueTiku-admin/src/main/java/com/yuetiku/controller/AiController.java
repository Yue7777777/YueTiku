package com.yuetiku.controller;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResponseFormat;
import com.alibaba.dashscope.common.Role;
import com.openai.client.OpenAIClient;
import com.openai.models.FileCreateParams;
import com.openai.models.FileObject;
import com.openai.models.FilePurpose;
import com.yuetiku.common.Result;
import com.yuetiku.context.AiJsonContext;
import com.yuetiku.dto.QuestionRequest;
import com.yuetiku.service.QuestionService;
import com.yuetiku.vo.AiQuestionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {
    private final ChatClient chatClient;
    private final OpenAIClient openAIClient;
    private final QuestionService questionService;

    @PostMapping("/file")
    public Result<List<AiQuestionVo>> getfile(@RequestParam("file") MultipartFile file) {
        try {
            // 2. 提取文件扩展名（作为临时文件后缀）
            String originalFilename = file.getOriginalFilename();
            String suffix = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))  // 如 .docx
                    : ".tmp";  // 无扩展名时用默认后缀

            log.info("后缀名为：{}",suffix);
            LocalDateTime now1 = LocalDateTime.now();
            // 将MultipartFile转换为Path
            Path tempFile = Files.createTempFile("upload_", suffix);
            try {
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

                // 上传文件到DashScope
                FileCreateParams fileParams = FileCreateParams.builder()
                        .file(tempFile)
                        .purpose(FilePurpose.of("file-extract"))
                        .build();

                FileObject fileObject = openAIClient.files().create(fileParams);
                String fileId = fileObject.id();
                log.info("fileId:{}", fileId);

                String content = chatClient.prompt()
                        .system("fileid://" + fileId)
                        .user(AiJsonContext.USER_TEXT)
                        .call()
                        .content();
                LocalDateTime now2 = LocalDateTime.now();
                long time1 = now1.until(now2, ChronoUnit.SECONDS);
                log.info("完成文件解析工作,花费时间{}",time1);

                //将文档分析提取加入到结构化输出的条件中
                Message systemMsg = Message.builder()
                        .role(Role.SYSTEM.getValue())
                        .content(AiJsonContext.CONTEXT)
                        .build();

                Message userMsg = Message.builder()
                        .role(Role.USER.getValue())
                        .content(content)
                        .build();

                Generation gen = new Generation();
                ResponseFormat jsonMode = ResponseFormat.builder().type("json_object").build();
                GenerationParam param = GenerationParam.builder()
                        .apiKey("sk-86efc77040c1423280dc57867fdc1300")
                        .model("qwen-flash")
                        .messages(Arrays.asList(systemMsg, userMsg))
                        .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                        .responseFormat(jsonMode)
                        .build();
                //提取信息
                GenerationResult call = gen.call(param);
                String result = call.getOutput().getChoices().get(0).getMessage().getContent();
                LocalDateTime now3 = LocalDateTime.now();
                long time2 = now2.until(now3, ChronoUnit.SECONDS);
                log.info("结构化输出完成（String）,花费时间为{}",time2);

                //转为json
                BeanOutputConverter<AiQuestionVo[]> outputConverter = new BeanOutputConverter<>(AiQuestionVo[].class);
                AiQuestionVo[] convert = outputConverter.convert(result);
                List<AiQuestionVo> question = Arrays.asList(convert);
                LocalDateTime now4 = LocalDateTime.now();
                long time3 = now3.until(now4, ChronoUnit.SECONDS);
                log.info("json格式转换完成，花费时间为{}",time3);

                return Result.success(question);
            }finally {
                Files.delete(tempFile);
            }
        } catch (Exception e) {
            log.error("处理文件失败", e);
            return Result.error("处理文件失败");
        }
    }

    /**
     * 批量导入AI解析的题目
     *
     * @param categoryId 分类ID
     * @param questions AI解析的题目列表
     * @return 导入结果统计
     */
    @PostMapping("/batch-import")
    public Result<QuestionService.BatchImportResult> batchImportQuestions(
            @RequestParam Long categoryId,
            @RequestBody List<AiQuestionVo> questions) {
        try {
            log.info("批量导入AI解析题目，分类ID: {}, 题目数量: {}", categoryId, questions.size());
            
            // 转换AI题目为QuestionRequest格式
            List<QuestionRequest> questionRequests = convertAiQuestionsToQuestionRequests(questions, categoryId);
            
            // 执行批量导入
            QuestionService.BatchImportResult result = questionService.batchImportQuestions(questionRequests);
            
            String message = String.format("批量导入完成，成功: %d, 失败: %d, 总数: %d", 
                    result.getSuccessCount(), result.getFailureCount(), result.getTotalCount());
            
            return Result.success(message, result);
        } catch (Exception e) {
            log.error("批量导入题目失败", e);
            return Result.error("批量导入题目失败: " + e.getMessage());
        }
    }

    /**
     * 将AI解析的题目转换为QuestionRequest格式
     */
    private List<QuestionRequest> convertAiQuestionsToQuestionRequests(List<AiQuestionVo> aiQuestions, Long categoryId) {
        return aiQuestions.stream().map(aiQuestion -> {
            QuestionRequest questionRequest = new QuestionRequest();
            questionRequest.setCategoryId(categoryId);
            questionRequest.setType(aiQuestion.getType());
            questionRequest.setTitle(aiQuestion.getTitle());
            questionRequest.setContent(aiQuestion.getContent());
            questionRequest.setExplanation(aiQuestion.getExplanation());
            questionRequest.setDifficulty(aiQuestion.getDifficulty());
            questionRequest.setPoints(1); // 默认分值
            questionRequest.setSource("AI导入");
            questionRequest.setStatus(1); // 正常状态

            // 转换选项
            if (aiQuestion.getOptions() != null && !aiQuestion.getOptions().isEmpty()) {
                List<QuestionRequest.QuestionOptionRequest> options = aiQuestion.getOptions().stream().map(aiOption -> {
                    QuestionRequest.QuestionOptionRequest option = new QuestionRequest.QuestionOptionRequest();
                    option.setOptionKey(aiOption.getOptionKey());
                    option.setOptionContent(aiOption.getOptionContent());
                    option.setIsCorrect(aiOption.getIsCorrect());
                    option.setSortOrder(aiOption.getSortOrder());
                    return option;
                }).collect(java.util.stream.Collectors.toList());
                questionRequest.setOptions(options);
            }

            // 转换答案
            if (aiQuestion.getAnswer() != null) {
                QuestionRequest.QuestionAnswerRequest answer = new QuestionRequest.QuestionAnswerRequest();
                answer.setAnswerType(aiQuestion.getAnswer().getAnswerType());
                answer.setCorrectAnswer(aiQuestion.getAnswer().getCorrectAnswer());
                answer.setAnswerExplanation(aiQuestion.getAnswer().getAnswerExplanation());
                questionRequest.setAnswer(answer);
            }

            return questionRequest;
        }).collect(java.util.stream.Collectors.toList());
    }
}
