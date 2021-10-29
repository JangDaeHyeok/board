package com.example.demo.handler;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.file.BoardFile;
import com.example.demo.board.file.BoardFileRepository;
import com.example.demo.util.UUIDUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Component
@Log4j2
public class BoardFileHandler {
	
	private static final String BOARD_CIRCUIT_BREAKER = "boardCircuitBreaker";
	
	// 자바 컴파일러가 람다식 에서 파라미터로 사용하는 변수와 로컬 변수를 구분을 하지 못하기 때문에 위에서 선언
	private String boardIdx = "";
	
	@Autowired
	BoardRepository boardRepository;
	@Autowired
	BoardFileRepository boardFileRepository;
	
	// board file save
	// 다중 파일저장 가능
	@Transactional
	@CircuitBreaker(name = BOARD_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	public Mono<ServerResponse> boardFileUpload(ServerRequest request) {
		Map<String, String> returnMap = new HashMap<>();
		boardIdx = boardIdx = UUIDUtil.createUUID();
		if(!request.pathVariable("boardIdx").equals("write")) {
			boardIdx = request.pathVariable("boardIdx");
		}
		Mono<Map> then = request.multipartData().map(it -> it.get("files"))
				 .flatMapMany(Flux::fromIterable)
				 .cast(FilePart.class)
				 .flatMap(it -> {
					String boardFileIdx = UUIDUtil.createUUID();
					String extension = it.filename().substring(it.filename().lastIndexOf("."));
					BoardFile boardFile = BoardFile.builder()
											.boardFileIdx(boardFileIdx)
											.boardIdx(boardIdx)
											.fileNm(boardFileIdx + extension)
											.originFileNm(it.filename())
											.filePath("/board/" + boardFileIdx + extension)
											.build();
					boardFileRepository.saveBoardFile(boardFile).subscribe();
					returnMap.put("result", "success");
					returnMap.put("boardIdx", boardIdx);
					return it.transferTo(Paths.get("board/" + boardFileIdx  + extension));
				 })
				 .then(Mono.just(returnMap));
		
		
		return ServerResponse.ok().body(then, Map.class);
	}
	
	// board file download
	@Transactional
	@CircuitBreaker(name = BOARD_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	public Mono<ServerResponse> boardFileDownload(ServerRequest request) {
		String fileNm = request.pathVariable("fileNm");
		String path = Paths.get("board/" + fileNm).toAbsolutePath().normalize().toString();
		Resource resource = new FileSystemResource(path);
        Mono<Resource> mapper = Mono.just(resource);  

        return ServerResponse.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileNm)
                .body(BodyInserters.fromProducer(mapper, Resource.class));
	}
	
	// circuitbreaker fallback
	public Mono<ServerResponse> fallback(ServerRequest request, Throwable t) {
        log.error("Fallback : " + t.getMessage());
        return ServerResponse.ok().body(Mono.just("boardFile CircuitBreaker"), String.class);
    }
}
