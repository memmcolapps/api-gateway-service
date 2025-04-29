package com.memmcol.apigatewayservice;


import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import reactor.netty.http.server.HttpServer;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();

        factory.addServerCustomizers(httpServer -> {
            // Start a separate HTTP redirect server on port 2020
            HttpServer redirectHttpServer = HttpServer.create()
                    .port(8080)
                    .route(routes ->
                            routes.route(req -> true, (request, response) -> {
                                String hostHeader = request.requestHeaders().get("Host");
                                String redirectHost;

                                if (hostHeader.contains(":")) {
                                    redirectHost = hostHeader.replaceFirst(":\\d+", ":8081");
                                } else {
                                    redirectHost = hostHeader + ":8081";
                                }

                                String redirectUrl = "https://" + redirectHost + request.uri();
                                return response.status(HttpResponseStatus.MOVED_PERMANENTLY)
                                        .header("Location", redirectUrl)
                                        .send();
                            })
                    );

            // Start the HTTP server separately
            redirectHttpServer.bindNow();

            // Return the original HTTPS server config (on 8081 via application.yml)
            return httpServer;
        });

        return factory;
    }
}


//
//import io.netty.handler.codec.http.HttpResponseStatus;
//import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
////import org.springframework.boot.web.reactive.context.WebApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.server.HttpServer;
//
//@Configuration
//public class HttpToHttpsRedirectConfig {
//
//    @Bean
//    public NettyReactiveWebServerFactory reactiveWebServerFactory() {
//        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
//
//        factory.addServerCustomizers(httpServer -> {
//            // Set up the HTTP redirect server
//            HttpServer redirectHttpServer = HttpServer.create()
//                    .port(2020) // HTTP port 2020
//                    .route(routes ->
//                            routes.route(req -> true, (request, response) -> {
//                                String host = request.requestHeaders().get("Host");
//                                String redirectUrl = "https://" + host + request.uri(); // Redirect to HTTPS
//                                return response.status(HttpResponseStatus.MOVED_PERMANENTLY)
//                                        .header("Location", redirectUrl)
//                                        .send();
//                            })
//                    );
//
//            redirectHttpServer.bindNow(); // Start the redirect server
//
//            return httpServer; // Main HTTPS server continues
//        });
//
//        return factory; // Return factory configuration
//    }
//}

///

//import io.netty.handler.codec.http.HttpResponseStatus;
//import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.netty.http.server.HttpServer;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//
//
//@Configuration
//public class HttpToHttpsRedirectConfig {
//
//    @Bean
//    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> webServerFactoryCustomizer() {
//        return factory -> {
//            factory.addServerCustomizers(httpServer -> {
//                // Set up HTTP server to listen on port 2020
//                HttpServer redirectHttpServer = HttpServer.create()
//                        .port(2020)
//                        .route(routes ->
//                                routes.route(req -> true, (request, response) -> {
//                                    String host = request.requestHeaders().get("Host");
//                                    String redirectUrl = "https://" + host + request.uri();
//                                    return response.status(HttpResponseStatus.MOVED_PERMANENTLY)
//                                            .header("Location", redirectUrl)
//                                            .send();
//                                })
//                        );
//                redirectHttpServer.bindNow(); // Start the HTTP redirect server
//                return httpServer;  // Proceed with the HTTPS server (handled by Spring Boot)
//            });
//        };
//    }
//}


