package com.example.demo.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class TestHandler {
	// 대혁씨 요청 
	public Mono<ServerResponse> boardReadiness(ServerRequest request) {
		return ServerResponse.ok().body(Mono.just("board OK"), String.class);
	}
}
