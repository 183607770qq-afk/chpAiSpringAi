package com.chp.chpaispringai.controller;


import com.chp.chpaispringai.agent.AIAgentService;
import com.chp.chpaispringai.service.RAGService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AIAgentController {

    private final AIAgentService agentService;
    private final RAGService ragService;

    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return agentService.chat(message);
    }

    @PostMapping("/knowledge/add")
    public String addKnowledge(@RequestParam String content) {
        ragService.addKnowledge(content);
        return "添加成功";
    }
}