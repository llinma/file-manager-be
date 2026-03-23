package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_link") // 对应数据库表名
public class KnowledgeLink {
    @TableId(type = IdType.ASSIGN_UUID) // 自动生成UUID主键
    private String id;                  // 关系ID
    private String sourceId;            // 源节点ID
    private String targetId;            // 目标节点ID
    private String relation;            // 关系类型（关联/依赖/包含）
}