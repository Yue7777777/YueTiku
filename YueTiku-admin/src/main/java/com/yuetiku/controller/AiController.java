package com.yuetiku.controller;

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

                return chatClient.prompt()
                        .system("fileid://"+fileId)
                        .user("总结一下文档的内容")
                        .call()
                        .content();
            }finally {
                Files.delete(tempFile);
            }
        } catch (Exception e) {
            log.error("处理文件失败", e);
            return "处理文件失败";
        }
    }
}
