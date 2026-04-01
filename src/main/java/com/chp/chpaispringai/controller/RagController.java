package com.chp.chpaispringai.controller;

import com.chp.chpaispringai.service.RAGService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final ChatClient dashScopeChatClient;

//    @Resource
//    private VectorStore vectorStore;

    @Autowired
    private RAGService ragService;

    public RagController(ChatClient.Builder builder) {
        this.dashScopeChatClient = builder.build();
    }
//
//    @GetMapping(value = "/chat", produces = "text/plain; charset=UTF-8")
//   public String generation(@RequestParam("userInput") String userInput) {
//        //发起聊天请求并处理响应
//        return dashScopeChatClient.prompt()
//                .user(userInput)
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .call()
//                .content();
//    }
    @RequestMapping("/ragTest")
    public Map<String,String> retrieveAndGenerate(@RequestParam("question") String question){
        String str = ragService.retrieveAndGenerate(question);
        return Map.of(question,str);

    }
}
