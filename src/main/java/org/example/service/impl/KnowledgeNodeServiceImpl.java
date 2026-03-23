package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.KnowledgeNode;
import org.example.mapper.KnowledgeNodeMapper;
import org.example.service.KnowledgeNodeService;
import org.springframework.stereotype.Service;

@Service // 标记为Spring Service
public class KnowledgeNodeServiceImpl extends ServiceImpl<KnowledgeNodeMapper, KnowledgeNode> implements KnowledgeNodeService {
    // 继承ServiceImpl，自动实现IService的所有方法
}