package com.chp.chpaispringai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;  // 核心：Spring AI 会自动注入 Milvus 实现

    @Autowired
    public RAGService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    /**
     * 1. 存入数据到 Milvus（自动向量化）
     */
    public void ingestData(String text) {
        // Spring AI 会自动调用配置的 Embedding 模型（如 Ollama）将文本转为向量
        vectorStore.add(List.of(new Document(text)));
        System.out.println("✅ 数据已存入 Milvus（自动向量化）");
    }

    /**
     * 2. 检索增强生成（使用 Milvus 做向量检索）
     */
    public String retrieveAndGenerate(String question) {
        // 1. 检索（Retrieval）- 调用 Milvus
        String context = retrieveFromMilvus(question);

        // 2. 生成（Generation）- 调用大模型
        String promptTemplate = """
                你是一个知识渊博的助手。请根据以下提供的上下文资料回答用户的问题。
                如果上下文资料中没有相关信息，请明确回答“我不知道”。
                
                上下文资料：
                {context}
                
                问题：
                {question}
                """;

        return chatClient.prompt()
                .user(prompt -> prompt
                        .text(promptTemplate)
                        .param("context", context)
                        .param("question", question))
                .call()
                .content();
    }

    /**
     * 核心：从 Milvus 检索相关文档
     */
    private String retrieveFromMilvus(String query) {
        // 调用 vectorStore.similaritySearch()，Spring AI 会自动：
        // 1. 将 query 文本向量化（通过配置的 EmbeddingModel）
        // 2. 在 Milvus 中执行向量相似度搜索
        // 3. 返回最相关的文档

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)                  // 取前3个最相似的
                        .similarityThreshold(0.7) // 相似度阈值，可自行调整
                        .build()
        );
        if (relevantDocs.isEmpty()) {
            return "未找到相关资料。";
        }

        // 将多个文档内容合并
        return relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 3. 快捷问答（使用 Spring AI 内置的 RetrievalAugmentationAdvisor）
     * 这是更简单的写法，效果与上面类似
     */
    public String askQuestion(String question) {
        return this.chatClient.prompt()
                .user(question)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
    }

    /**
     * 4. 获取 Milvus 中的文档数量（调试用）
     */
    public long getDocumentCount() {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("") // 空查询，取所有
                        .topK(1000)
                        .build()
        ).size();
    }

    public String searchContext(String query) {
        // 1. 向量相似度检索
        List<Document> docs = vectorStore.similaritySearch(query);

        // 2. 用lambda+getText() 100%兼容，无类型问题
        return docs.stream()
                .map(doc -> doc.getText()) // 替代getContent()，适配1.0+新API
                .collect(Collectors.joining("\n---\n"));
    }
    public void addKnowledge(String content) {
        vectorStore.add(List.of(new Document(content)));
    }
}