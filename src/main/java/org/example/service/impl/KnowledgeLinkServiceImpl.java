package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.KnowledgeLink;
import org.example.mapper.KnowledgeLinkMapper;
import org.example.service.KnowledgeLinkService;
import org.springframework.stereotype.Service;

@Service // 标记为Spring Service
public class KnowledgeLinkServiceImpl extends ServiceImpl<KnowledgeLinkMapper, KnowledgeLink> implements KnowledgeLinkService {
    // 继承ServiceImpl，自动实现IService的所有方法
}