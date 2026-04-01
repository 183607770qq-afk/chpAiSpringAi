package com.chp.chpaispringai.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    // 1. 用于存储文档的列表
    private List<String> docs = List.of(
            "我爱北京天安门",
            "今晚同学们都很热情",
            "那家餐馆的饭菜非常美味"
    );

    // 2. 注入 Spring AI 的 Embedding 客户端
    private final EmbeddingModel embeddingModel;

    private List<float[]> embed;
    // 构造函数注入
    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;

        this.embed = this.embeddingModel.embed(docs);
    }

    /**
     * 核心方法：查找最相似的文本
     * @param message 用户输入的查询文本
     * @return 最相似的文本内容
     */
    public String findSimText(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "请输入有效文本";
        }

        try {
            // --- 1. 向量化 (Embedding) ---
            // 将用户输入转换为向量
            float[] inputEmbedding = embeddingModel.embed(message);

            // 将文档库中的所有文本批量转换为向量
//            EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(docs);
//            List<float[]>docEmbeddings = embeddingResponse.getResults().stream()
//                    .map(r -> r.getOutput())
//                    .collect(Collectors.toList());
            EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(docs);
            List<float[]>docEmbeddings = embeddingResponse.getResults().stream()
                    .map(r -> r.getOutput())
                    .collect(Collectors.toList());

            // --- 2. 计算余弦相似度 ---
            double maxScore = -1.0;
            String bestMatch = docs.get(0); // 默认返回第一个

            for (int i = 0; i < docEmbeddings.size(); i++) {
                double score = calculateCosineSimilarity(inputEmbedding, embed.get(i));

                if (score > maxScore) {
                    maxScore = score;
                    bestMatch = docs.get(i);
                }
            }

            System.out.println("匹配得分: " + maxScore);
            return bestMatch;

        } catch (Exception e) {
            e.printStackTrace();
            return "处理出错: " + e.getMessage();
        }
    }

    /**
     * 计算两个向量之间的余弦相似度
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