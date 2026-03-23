package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_node") // 对应数据库表名
public class KnowledgeNode {
    @TableId(type = IdType.ASSIGN_UUID) // 自动生成UUID主键
    private String id;                  // 节点ID
    private String title;               // 文档标题
    private String description;         // 文档简介
    private String content;             // 文档HTML内容
    private String tags;                // 标签（逗号分隔）
    private String color;               // 节点颜色
    private LocalDateTime createTime;   // 创建时间

    // 👇 新增这两个字段
    private String fileName;   // PDF原文件名
    private String pdfUrl;    // PDF文件存储路径/下载地址
}