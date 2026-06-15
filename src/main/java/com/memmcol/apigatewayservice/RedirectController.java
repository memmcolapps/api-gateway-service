package com.memmcol.apigatewayservice;


import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class RedirectController {

    @GetMapping("/**")
    public Mono<Void> redirect(ServerHttpRequest request, ServerHttpResponse response) {

        URI uri = request.getURI();

        String httpsUrl = "https://" + uri.getHost() + uri.getRawPath();

        response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        response.getHeaders().setLocation(URI.create(httpsUrl));

        return response.setComplete();
    }
}