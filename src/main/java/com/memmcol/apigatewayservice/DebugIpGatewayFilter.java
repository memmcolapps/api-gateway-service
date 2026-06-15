package com.memmcol.apigatewayservice;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class DebugIpGatewayFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var headers = exchange.getRequest().getHeaders();

        System.out.println("REMOTE ADDRESS: " +
                exchange.getRequest().getRemoteAddress());

        System.out.println("X-FORWARDED-FOR: " +
                headers.getFirst("X-Forwarded-For"));

        System.out.println("X-REAL-IP: " +
                headers.getFirst("X-Real-IP"));

        return chain.filter(exchange);
    }
}