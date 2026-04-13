package com.chp.chpaispringai.agent.function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@Configuration
public class AgentFunctions {

    public record OrderRequest(String userId) {}
    public record LogisticsRequest(String orderNo) {}

    @Bean
    public Function<OrderRequest, String> queryUserOrder() {
        return request ->
                "用户ID：" + request.userId() + "\n" +
                "订单：ORDER_20250413001\n" +
                "商品：MacBook Air M4 24G+512G\n" +
                "状态：待发货";
    }

    @Bean
    public Function<LogisticsRequest, String> queryLogistics() {
        return req ->
                "订单号：" + req.orderNo() + "\n" +
                "物流：顺丰速运 SF102304995\n" +
                "当前状态：仓库处理中";
    }
}