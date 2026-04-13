package com.chp.chpaispringai.agent.memory;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ConversationMemory {
    private final List<Message> history = new ArrayList<>();

    public void addUserMessage(String content) {
        history.add(new UserMessage(content));
    }

    public void addAssistantMessage(String content) {
        history.add(new AssistantMessage(content));
    }

    public List<Message> getHistory() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }
}