package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.KnowledgeLink;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标记为MyBatis Mapper
public interface KnowledgeLinkMapper extends BaseMapper<KnowledgeLink> {
    // 继承BaseMapper，无需手动写CRUD方法
}