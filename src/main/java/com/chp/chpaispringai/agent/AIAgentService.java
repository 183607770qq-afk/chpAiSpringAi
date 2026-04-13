package com.chp.chpaispringai.agent;

import com.chp.chpaispringai.agent.memory.ConversationMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIAgentService {

    private final ChatClient chatClient;
    private final ConversationMemory memory;
    @Autowired
    public AIAgentService(ChatClient.Builder chatClientBuilder, ConversationMemory memory) {
        this.chatClient = chatClientBuilder.build();
        this.memory = memory;
    }
    public String chat(String userInput) {
        memory.addUserMessage(userInput);
        List<Message> messages = memory.getHistory();

        String reply = chatClient.prompt()
                .messages(messages)
                .functions("queryUserOrder", "queryLogistics")
                .call()
                .content();

        memory.addAssistantMessage(reply);
        return reply;
    }
}