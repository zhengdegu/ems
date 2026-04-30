package com.ems.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.dto.R;
import com.ems.entity.Attachment;
import com.ems.mapper.AttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${ems.upload.path:./uploads}")
    private String uploadPath;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @PostMapping("/upload")
    public R<?> upload(@RequestParam("file") MultipartFile file,
                       @RequestParam(required = false) String bizType,
                       @RequestParam(required = false) Long bizId) {
        if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        try {
            // 按年月组织目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            File dir = new File(uploadPath + "/" + datePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成存储文件名
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
            String filePath = datePath + "/" + storedName;

            // 保存文件
            file.transferTo(new File(dir, storedName));

            // 获取当前用户
            String uploader = "unknown";
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                uploader = auth.getPrincipal().toString();
            }

            // 保存记录
            Attachment attachment = new Attachment();
            attachment.setFileName(originalName);
            attachment.setFilePath(filePath);
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setBizType(bizType);
            attachment.setBizId(bizId);
            attachment.setUploader(uploader);
            attachment.setCreateTime(LocalDateTime.now());
            attachmentMapper.insert(attachment);

            return R.ok(attachment);
        } catch (IOException e) {
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Attachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(uploadPath + "/" + attachment.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    @GetMapping("/list")
    public R<?> list(@RequestParam(required = false) String bizType,
                     @RequestParam(required = false) Long bizId) {
        LambdaQueryWrapper<Attachment> wrapper = new LambdaQueryWrapper<>();
        if (bizType != null) {
            wrapper.eq(Attachment::getBizType, bizType);
        }
        if (bizId != null) {
            wrapper.eq(Attachment::getBizId, bizId);
        }
        wrapper.orderByDesc(Attachment::getCreateTime);
        List<Attachment> list = attachmentMapper.selectList(wrapper);
        return R.ok(list);
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        Attachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            return R.fail("附件不存在");
        }

        // 删除物理文件
        File file = new File(uploadPath + "/" + attachment.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        // 删除记录
        attachmentMapper.deleteById(id);
        return R.ok();
    }
}
