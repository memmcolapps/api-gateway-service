package com.memmcol.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

//@EnableCircuitBreaker
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServiceApplication.class, args);
    }

//    @Bean(name = "resilience4jThreadPoolBulkhead")
//    public ThreadPoolBulkheadProperties threadPoolBulkheadProperties() {
//        return new ThreadPoolBulkheadProperties();
//    }


}

