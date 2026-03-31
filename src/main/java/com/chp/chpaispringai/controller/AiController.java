package com.chp.chpaispringai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AiController {

    private final ChatClient chatClient;

    // 注入 ChatClient.Builder
    public AiController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // 最简单的同步对话接口
    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你好，请介绍一下你自己") String message) {
        // 1. prompt: 设置提示词
        // 2. call: 同步阻塞调用
        // 3. content: 获取文本内容
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()       // 开启流式模式
                .content();     // 返回 Flux<String>
    }
}