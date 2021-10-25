package com.example.demo.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.board.BoardRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
@Component
@Log4j2
public class BoardFileHandler {
	
	private static final String BOARD_CIRCUIT_BREAKER = "boardCircuitBreaker";
	
	@Autowired
	BoardRepository boardRepository;
	
	// board file save
	@Transactional
	@CircuitBreaker(name = BOARD_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	public Mono<ServerResponse> boardFileAdd(ServerRequest request) {
//		return request.multipartData().map(it -> it.get("files"))
//				.cast(FilePart.class)
//				.flatMap(t -> {
//					System.out.println(t);
//					t.transferTo(Paths.get("/board/file" + t.filename()));
//					return ServerResponse.ok().body(Mono.just("board file upload test"), String.class);
//				});
//		 Mono<String> then = request.multipartData().map(it -> it.get("files"))
//			        .flatMapMany(Flux::fromIterable)
//			        .cast(FilePart.class)
//			        .flatMap(it -> it.transferTo(Paths.get("/tmp/" + it.filename())))
//			        .then(Mono.just("OK"));
					return ServerResponse.ok().body(Mono.just("board file upload test"), String.class);
	}
	
	
	// circuitbreaker fallback
	public Mono<ServerResponse> fallback(ServerRequest request, Throwable t) {
        log.error("Fallback : " + t.getMessage());
        return ServerResponse.ok().body(Mono.just("boardFile CircuitBreaker"), String.class);
    }
}
