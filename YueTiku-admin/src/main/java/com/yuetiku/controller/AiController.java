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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {
    private final ChatClient chatClient;
    private final OpenAIClient openAIClient;

    @PostMapping("/file")
    public String getfile(@RequestParam("file") MultipartFile file) {
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
                        .user("提取一下文档的的内容")
                        .call()
                        .content();

                log.info("content:{}", content);

                //将文档分析提取加入到结构化输出的条件中
                Message systemMsg = Message.builder()
                        .role(Role.SYSTEM.getValue())
                        .content("""
                                请你从用户给出的题目内容中提取出题目信息并返回一段严格的 JSON，包含以下字段：
                                        - type（题目的类型，只能是single,multiple,fill,answer,judge中的一个）
                                        - title（题目的标题，如果没有就填相应题型的中文,如single对应单选题）
                                        - content（题目的问题部分,选择题不包括选项）
                                        - explanation（题目的解析部分，如果没有你就自己根据题目的问题部分给出解析）
                                        - difficulty（题目的难度,必需是easy,medium,hard中的一个，如果没有就默认medium）
                                """)
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
                return result;
            }finally {
                Files.delete(tempFile);
            }
        } catch (Exception e) {
            log.error("处理文件失败", e);
            return "处理文件失败";
        }
    }
}
