package org.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Tag(name = "AI 对话", description = "对接通义千问大模型，SSE 流式输出")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private String apiKey = "sk-4cacc3cadeed4a0abde4238cd127019f";
    private String model = "qwen-turbo";
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Operation(summary = "发送聊天消息", description = "传入 messages 字段，返回 SSE 流式文本")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody Map<String, Object> params) {
        String userMessage = (String) params.get("messages");
        SseEmitter emitter = new SseEmitter(0L);

        executor.execute(() -> {
            try {
                if (userMessage == null || userMessage.trim().isEmpty()) {
                    emitter.send(SseEmitter.event().data("请输入内容"));
                    emitter.complete();
                    return;
                }

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);

                JSONObject input = new JSONObject();
                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> msg = new HashMap<>();
                msg.put("role", "user");
                msg.put("content", userMessage);
                messages.add(msg);
                input.put("messages", messages);
                requestBody.put("input", input);

                JSONObject parameters = new JSONObject();
                parameters.put("incremental_output", true);
                requestBody.put("parameters", parameters);

                URL url = new URL(baseUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "text/event-stream");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBody.toJSONString().getBytes(StandardCharsets.UTF_8));
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
                );

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data:")) {
                        String data = line.substring(5).trim();
                        if ("[DONE]".equals(data)) {
                            break;
                        }

                        try {
                            JSONObject json = JSON.parseObject(data);
                            String text = "";

                            if (json.containsKey("output")) {
                                JSONObject output = json.getJSONObject("output");
                                if (output != null && output.containsKey("choices")) {
                                    JSONArray choices = output.getJSONArray("choices");
                                    if (choices != null && choices.size() > 0) {
                                        text = choices.getJSONObject(0).getString("text");
                                    }
                                }
                            } else if (json.containsKey("message")) {
                                text = "错误：" + json.getString("message");
                            }

                            if (!text.isEmpty()) {
                                emitter.send(SseEmitter.event().data(text));
                            }
                        } catch (Exception ignored) {}
                    }
                }

                reader.close();
                conn.disconnect();

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("系统异常"));
                } catch (Exception ex) {}
            } finally {
                emitter.complete();
            }
        });

        return emitter;
    }
}