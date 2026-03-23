package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.KnowledgeNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标记为MyBatis Mapper
public interface KnowledgeNodeMapper extends BaseMapper<KnowledgeNode> {
    // 继承BaseMapper，无需手动写CRUD方法
}