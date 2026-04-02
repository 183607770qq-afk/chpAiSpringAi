package com.chp.chpaispringai.controller;

import com.chp.chpaispringai.service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RAGController {

    private final RAGService ragService;

    @Autowired
    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    /**
     * 1. 文本入库（向量化存入 Milvus）
     * POST http://localhost:8080/rag/ingest
     * body: { "text": "你的内容" }
     */
    @PostMapping("/ingest")
    public Map<String, String> ingestData(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        ragService.ingestData(text);

        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "文本已成功向量化并存入 Milvus");
        return result;
    }

    /**
     * 2. 问答接口（RAG 检索增强生成）
     * GET http://localhost:8080/rag/ask?question=你的问题
     */
    @GetMapping("/ask")
    public Map<String, String> askQuestion(@RequestParam String question) {
        String answer = ragService.retrieveAndGenerate(question);

        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("question", question);
        result.put("answer", answer);
        return result;
    }

    /**
     * 3. 快捷问答（Spring AI 内置 Advisor 版）
     * GET http://localhost:8080/rag/ask-simple?question=你的问题
     */
    @GetMapping("/ask-simple")
    public Map<String, String> askSimple(@RequestParam String question) {
        String answer = ragService.askQuestion(question);

        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("question", question);
        result.put("answer", answer);
        return result;
    }

    /**
     * 4. 获取 Milvus 中已存储的文档数量
     * GET http://localhost:8080/rag/count
     */
    @GetMapping("/count")
    public Map<String, Object> getCount() {
        long count = ragService.getDocumentCount();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("documentCount", count);
        return result;
    }
}