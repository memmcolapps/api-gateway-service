package com.memmcol.apigatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class ClientIpFilter implements GlobalFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ClientIpFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var request = exchange.getRequest();

        String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        String forwarded = request.getHeaders().getFirst("X-Forwarded-For");

        log.info("Remote IP (TCP): {}", ip);
        log.info("X-Forwarded-For (incoming): {}", forwarded);

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-Real-IP", ip)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
