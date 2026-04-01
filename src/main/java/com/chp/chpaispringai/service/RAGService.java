// src/main/java/com/example/demo/service/RAGService.java
package com.chp.chpaispringai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RAGService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    // 模拟的知识库
    private final List<String> knowledgeBase = List.of(
            "北京是中国的首都，拥有悠久的历史和文化。",
            "长城是北京的著名景点，全长超过2万公里。",
            "故宫位于北京市中心，是明清两代的皇家宫殿。",
            "上海是中国的经济中心，拥有东方明珠塔。",
            "杭州以西湖闻名，是浙江省的省会。"
    );


    @Autowired
    public RAGService(ChatClient.Builder chatClientBuilder, EmbeddingModel embeddingModel) {
        // 构建 ChatClient
        this.chatClient = chatClientBuilder.build();
        this.embeddingModel = embeddingModel;
    }

    /**
     * RAG 核心方法
     *
     * @param question 用户的问题
     * @return AI 生成的答案
     */
    public String retrieveAndGenerate(String question) {
        // 1. 检索 (Retrieval)
        String context = findMostRelevantContext(question);

        // 2. 生成 (Generation)
        // 构造提示词模板
        String promptTemplate = """
                你是一个知识渊博的助手。请根据以下提供的上下文资料回答用户的问题。
                如果上下文资料中没有相关信息，请明确回答“我不知道”。
                
                上下文资料：
                {context}
                
                问题：
                {question}
                """;

        // 调用大模型生成回答
        return chatClient
                .prompt() // 创建一个新的 Prompt
                .user(prompt -> prompt // 设置用户消息
                        .text(promptTemplate) // 使用模板
                        .param("context", context) // 替换模板中的 {context}
                        .param("question", question)) // 替换模板中的 {question}
                .call() // 执行调用
                .content(); // 获取返回的内容
    }

    /**
     * 检索最相关的上下文
     */
    private String findMostRelevantContext(String question) {
        // 将用户问题向量化
        float[] questionEmbedding = embeddingModel.embed(question);

        double maxScore = -1.0;
        String bestContext = "未找到相关资料。";

        // 遍历知识库，计算相似度
        for (String doc : knowledgeBase) {
            float[] docEmbedding = embeddingModel.embed(doc);
            double score = calculateCosineSimilarity(questionEmbedding, docEmbedding);

            if (score > maxScore) {
                maxScore = score;
                bestContext = doc;
            }
        }

        return bestContext;
    }

    /**
     * 计算两个向量的余弦相似度
     */
    private double calculateCosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // 遍历数组索引
        for (int i = 0; i < vec1.length; i++) {
            double a = vec1[i];
            double b = vec2[i];
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}