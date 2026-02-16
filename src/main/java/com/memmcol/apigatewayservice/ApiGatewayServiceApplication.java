package com.memmcol.apigatewayservice;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

//@EnableCircuitBreaker
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(ApiGatewayServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServiceApplication.class, args);
    }

    @Bean
    public GlobalFilter metricsUriTaggingFilter(MeterRegistry registry) {
        return (exchange, chain) -> {
            String actualPath = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod() != null
                    ? exchange.getRequest().getMethod().name()
                    : "UNKNOWN";

            Timer.Sample sample = Timer.start(registry);

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        HttpStatusCode status = exchange.getResponse().getStatusCode() != null
                                ? exchange.getResponse().getStatusCode()
                                : HttpStatus.INTERNAL_SERVER_ERROR;

                        log.info("Capturing metric for [{} {}] -> {}", method, actualPath, status);

                        sample.stop(
                                Timer.builder("http.server.requests") // use default server metric
                                        .tag("method", method)
                                        .tag("uri", actualPath)
                                        .tag("status", String.valueOf(status.value()))
                                        .register(registry)
                        );
                    }));
        };
    }

    @Bean
    public GlobalFilter forwardedHeaderFilter() {
        return (exchange, chain) -> {
            String clientIp = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : null;

            if (clientIp != null) {
                var modifiedRequest = exchange.getRequest().mutate()
                        .header("X-Forwarded-For", clientIp)
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
            return chain.filter(exchange);
        };
    }



}

