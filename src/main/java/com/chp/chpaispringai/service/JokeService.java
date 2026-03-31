import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JokeService {

    // 注入配置的模板字符串
    @Value("${spring.ai.prompt.template}")
    private String promptTemplate;

    private final ChatClient chatClient; // Spring AI 的 Chat 客户端

    public JokeService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 生成笑话的核心方法
     * @param userInput 用户输入（如“讲个关于程序员的笑话”）
     * @return JSON 格式的笑话（含 joke 和 punchline）
     */
    public Mono<String> generateJoke(String userInput) {
        // 1. 创建 PromptTemplate，支持 SpEL 表达式 {user_input}
        PromptTemplate template = new PromptTemplate(promptTemplate, Map.of("user_input", userInput));

        // 2. 渲染模板：将 {user_input} 替换为实际参数
        Prompt prompt = template.create();

        // 3. 调用模型，返回流式响应（Flux<String>）
        return chatClient.call(prompt)
                .getContent(); // 提取模型返回的文本内容
    }
}