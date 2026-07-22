package com.memmcol.apigatewayservice;


import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Profile;
import reactor.netty.http.server.HttpServer;


@Profile({"window"})
@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();

        factory.addServerCustomizers(httpServer -> {
            // Start a separate HTTP redirect server on port 2020
            HttpServer redirectHttpServer = HttpServer.create()
                    .port(8088)
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
