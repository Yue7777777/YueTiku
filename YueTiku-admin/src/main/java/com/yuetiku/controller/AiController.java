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
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {
    private final ChatClient chatClient;
    private final OpenAIClient openAIClient;

    @PostMapping("/file")
    public Result<List<AiQuestionVo>> getfile(@RequestParam("file") MultipartFile file) {
        try {
            // 2. 提取文件扩展名（作为临时文件后缀）
            String originalFilename = file.getOriginalFilename();
            String suffix = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))  // 如 .docx
                    : ".tmp";  // 无扩展名时用默认后缀

            log.info("后缀名为：{}",suffix);
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
                        .model("qwen-plus")
                        .messages(Arrays.asList(systemMsg, userMsg))
                        .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                        .responseFormat(jsonMode)
                        .build();
                //提取信息
                GenerationResult call = gen.call(param);
                String result = call.getOutput().getChoices().get(0).getMessage().getContent();

                //转为json
                BeanOutputConverter<AiQuestionVo[]> outputConverter = new BeanOutputConverter<>(AiQuestionVo[].class);
                AiQuestionVo[] convert = outputConverter.convert(result);
                List<AiQuestionVo> question = Arrays.asList(convert);

                return Result.success(question);
            }finally {
                Files.delete(tempFile);
            }
        } catch (Exception e) {
            log.error("处理文件失败", e);
            return Result.error("处理文件失败");
        }
    }
}
