package org.example.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.example.common.Result;
import org.example.entity.KnowledgeNode;
import org.example.service.KnowledgeNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.LocalDateTime;

@RestController
public class FileController {

    // 文件存储路径（application.yml 配置）
    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private KnowledgeNodeService nodeService;

    /**
     * 上传PDF文件并创建知识节点
     */
    @PostMapping("/api/file/upload-pdf")
    public Result<KnowledgeNode> uploadPDF(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "color", defaultValue = "#1890ff") String color) throws IOException {

        // 1. 校验文件类型
        if (!file.getOriginalFilename().endsWith(".pdf")) {
            return Result.error("仅支持上传PDF文件！");
        }

        // 2. 创建存储目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 3. 生成唯一文件名
        String fileId = IdWorker.getIdStr();
        String originalFileName = file.getOriginalFilename();
        String saveFileName = fileId + "_" + originalFileName;
        String savePath = uploadPath + File.separator + saveFileName;

        // 4. 保存文件到服务器
        File saveFile = new File(savePath);
        file.transferTo(saveFile);

        // 5. 创建知识节点
        KnowledgeNode node = new KnowledgeNode();
        node.setId(fileId);
        node.setTitle(title);
        node.setDescription(description);
        node.setTags(tags);
        node.setColor(color);
        node.setFileName(originalFileName);
        node.setPdfUrl("/api/file/download/" + fileId); // 下载地址
        node.setCreateTime(LocalDateTime.now());
        nodeService.save(node);

        return Result.success(node);
    }

    /**
     * 下载PDF文件
     */
    @GetMapping("/api/file/download/{fileId}")
    public void downloadPDF(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        // 1. 查找文件（实际项目建议用数据库关联file_storage表）
        File uploadDir = new File(uploadPath);
        File[] files = uploadDir.listFiles((dir, name) -> name.startsWith(fileId) && name.endsWith(".pdf"));
        if (files == null || files.length == 0) {
            response.sendError(404, "文件不存在");
            return;
        }

        File pdfFile = files[0];
        String originalFileName = pdfFile.getName().substring(pdfFile.getName().indexOf("_") + 1);

        // 2. 设置响应头
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(originalFileName, "UTF-8"));
        response.setContentLength((int) pdfFile.length());

        // 3. 写入响应流
        Files.copy(pdfFile.toPath(), response.getOutputStream());
    }

    @GetMapping("/api/file/preview/{fileId}")
    public void previewPDF(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        // ========== 1. 调试日志（关键：帮你定位文件问题） ==========
        File uploadDir = new File(uploadPath);
        System.out.println("===== PDF预览接口调试 =====");
        System.out.println("1. 文件存储目录：" + uploadDir.getAbsolutePath());
        System.out.println("2. 查找的fileId：" + fileId);
        System.out.println("3. 目录是否存在：" + uploadDir.exists());

        // 打印目录下所有文件，方便排查
        String[] allFiles = uploadDir.list();
//        System.out.println("4. 目录下所有文件：" + Arrays.toString(allFiles));

        // ========== 2. 兼容两种文件名匹配规则 ==========
        File[] files = null;
        // 规则1：匹配 "fileId_xxx.pdf"（以fileId开头）
        files = uploadDir.listFiles((dir, name) ->
                name.startsWith(fileId) && name.endsWith(".pdf")
        );

        // 如果规则1没找到，用规则2：匹配 "xxx_fileId.pdf"（以fileId结尾）
        if (files == null || files.length == 0) {
            files = uploadDir.listFiles((dir, name) ->
                    name.endsWith(fileId + ".pdf")
            );
        }

        // ========== 3. 文件不存在处理 ==========
        if (files == null || files.length == 0) {
            System.out.println("5. 未找到匹配的PDF文件");
            response.sendError(404, "文件不存在");
            return;
        }

        // ========== 4. 找到文件，设置预览响应头 ==========
        File pdfFile = files[0];
        System.out.println("5. 匹配到文件：" + pdfFile.getName());

        // 核心：告诉浏览器预览（inline）而非下载（attachment）
        response.setContentType("application/pdf"); // 固定PDF类型
        response.setHeader("Content-Disposition", "inline; filename=\"" + pdfFile.getName() + "\"");
        // 跨域配置（生产环境替换为你的前端域名，比如 http://localhost:3000）
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");

        // ========== 5. 流式返回PDF文件 ==========
        Files.copy(pdfFile.toPath(), response.getOutputStream());
        System.out.println("6. PDF预览流返回成功");
    }


}