package org.example.controller;

import org.example.common.Result;
import org.example.entity.KnowledgeNode;
import org.example.entity.KnowledgeLink;
import org.example.service.KnowledgeNodeService;
import org.example.service.KnowledgeLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge") // 接口前缀
public class KnowledgeController {

    @Autowired
    private KnowledgeNodeService nodeService; // 注入节点Service

    @Autowired
    private KnowledgeLinkService linkService; // 注入关系Service

    // 1. 获取知识图谱数据（大屏用）
    @GetMapping("/graph")
    public Result<Map<String, Object>> getGraphData() {
        List<KnowledgeNode> nodes = nodeService.list(); // 查询所有节点
        List<KnowledgeLink> links = linkService.list(); // 查询所有关系
        Map<String, Object> data = new HashMap<>();
        data.put("nodes", nodes);
        data.put("links", links);
        return Result.success(data); // 返回成功结果
    }

    // 2. 根据ID查询文档详情（预览用）
    @GetMapping("/node/{id}")
    public Result<KnowledgeNode> getNodeById(@PathVariable String id) {
        KnowledgeNode node = nodeService.getById(id); // 根据ID查询节点
        return Result.success(node);
    }

    // 3. 新标签页打开文档（直接返回HTML）
    @GetMapping("/node/{id}/view")
    public void viewNode(@PathVariable String id, HttpServletResponse response) throws IOException {
        KnowledgeNode node = nodeService.getById(id);
        response.setContentType("text/html;charset=utf-8"); // 设置响应类型为HTML
        response.getWriter().write(node.getContent()); // 返回文档HTML内容
    }
}