package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "知识图谱", description = "知识图谱 CRUD 接口，返回节点和关系数据")
@RestController
@RequestMapping("/api/knowledge") // 接口前缀
public class KnowledgeController {

    @Autowired
    private KnowledgeNodeService nodeService; // 注入节点Service

    @Autowired
    private KnowledgeLinkService linkService; // 注入关系Service

    @Operation(summary = "获取知识图谱全量数据", description = "返回所有节点和关系，供前端大屏渲染")
    @GetMapping("/graph")
    public Result<Map<String, Object>> getGraphData() {
        List<KnowledgeNode> nodes = nodeService.list(); // 查询所有节点
        List<KnowledgeLink> links = linkService.list(); // 查询所有关系
        Map<String, Object> data = new HashMap<>();
        data.put("nodes", nodes);
        data.put("links", links);
        return Result.success(data); // 返回成功结果
    }

    @Operation(summary = "根据 ID 查询节点详情", description = "返回单个知识节点的完整信息")
    @GetMapping("/node/{id}")
    public Result<KnowledgeNode> getNodeById(
            @Parameter(description = "节点 ID") @PathVariable String id) {
        KnowledgeNode node = nodeService.getById(id); // 根据ID查询节点
        return Result.success(node);
    }

    @Operation(summary = "新标签页查看文档", description = "直接返回文档 HTML 内容，在新标签页渲染")
    @GetMapping("/node/{id}/view")
    public void viewNode(
            @Parameter(description = "节点 ID") @PathVariable String id,
            HttpServletResponse response) throws IOException {
        KnowledgeNode node = nodeService.getById(id);
        response.setContentType("text/html;charset=utf-8"); // 设置响应类型为HTML
        response.getWriter().write(node.getContent()); // 返回文档HTML内容
    }
}