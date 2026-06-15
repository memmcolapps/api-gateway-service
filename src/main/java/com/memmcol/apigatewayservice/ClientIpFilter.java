package com.memmcol.apigatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


//@Component
//public class ClientIpFilter implements GlobalFilter {
//
//    private static final Logger log =
//            LoggerFactory.getLogger(ClientIpFilter.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        var request = exchange.getRequest();
//
//        String clientIp = exchange.getRequest().getRemoteAddress() != null
//                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
//                : "unknown";
//
////        String clientIp =  exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
//
//        String forwarded = request.getHeaders().getFirst("X-Forwarded-For");
//
//        log.info("Remote IP (TCP): {}", clientIp);
//        log.info("X-Forwarded-For (incoming): {}", forwarded);
//
//        ServerHttpRequest mutatedRequest = exchange.getRequest()
//                .mutate()
//                .headers(h -> {
//                    h.remove("X-Forwarded-For");
//                    h.remove("X-Real-IP");
//                    h.set("X-Client-IP", clientIp);
//                })
//                .build();
//
//        return chain.filter(exchange.mutate().request(mutatedRequest).build());
//    }
//}


@Component
public class ClientIpFilter implements GlobalFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ClientIpFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var request = exchange.getRequest();

        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        String realIp = request.getHeaders().getFirst("X-Real-IP");

        String clientIp;

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            clientIp = forwardedFor.split(",")[0].trim();
        } else if (realIp != null) {
            clientIp = realIp;
        } else {
            var remote = request.getRemoteAddress();
            clientIp = (remote != null && remote.getAddress() != null)
                    ? remote.getAddress().getHostAddress()
                    : "unknown";
        }

        log.info("Client IP resolved: {}", clientIp);
        log.info("X-Forwarded-For: {}", forwardedFor);
        log.info("RemoteAddress: {}", request.getRemoteAddress());

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Client-IP", clientIp)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}