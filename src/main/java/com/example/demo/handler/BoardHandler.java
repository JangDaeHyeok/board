package com.example.demo.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.board.Board;
import com.example.demo.board.BoardRepository;
import com.example.demo.board.file.BoardFile;
import com.example.demo.board.file.BoardFileRepository;
import com.example.demo.util.UUIDUtil;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

//@RequiredArgsConstructor
@Component
@Log4j2
public class BoardHandler {
	
	private static final String BOARD_CIRCUIT_BREAKER = "boardCircuitBreaker";
	
	@Autowired
	BoardRepository boardRepository;
	@Autowired
	BoardFileRepository boardFileRepository;
	
	private final String totalURL = "http://10.107.154.32:8092";	// commit 용
//	private String totalURL = "http://localhost:8084";				// local 용
	
	// board list
	@RateLimiter(name = BOARD_CIRCUIT_BREAKER)
	@Bulkhead(name = BOARD_CIRCUIT_BREAKER)
	@CircuitBreaker(name = BOARD_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	public Mono<ServerResponse> boardList(ServerRequest request) {
		Mono<Map> acceptData = request.bodyToMono(HashMap.class);
		Map<Object, Object> returnMap = new HashMap<>();
		
		return acceptData.flatMap(s -> {
			String viewType = s.get("viewType").toString();
			// 게시판 리스트
			if(viewType.equals("list")) {
				// 페이징
				int paging = 1;	// defalut page
				if(s.get("paging") != null) {
					paging = Integer.valueOf(s.get("paging").toString()) - 1;
				}
				int size = Integer.valueOf(s.get("size").toString());
//				Sort sort = Sort.by("reg_dt").descending();
//				Mono<List<Board>> list = boardRepository.findAll(sort).skip(paging * size).take(size).collectList();
				String URL = totalURL + "/user/list/userNm";
				Board board = Board.builder()
							.boardType(s.get("boardType").toString())
							.delYn("N")
							.build();
				if(s.get("searchType") != null && !s.get("searchType").equals("")) {
					board.setSearchType(s.get("searchType").toString());
					board.setSearchKeyword(s.get("searchKeyword").toString());
				}
				Mono<Integer> totalCnt = boardRepository.findTotalCount(board);
				Mono<List<Board>> list = boardRepository.findAllPaging(board, paging * size, size).collectList();
				
				// [::::::::::: webclient ::::::::::::]
				// webclient 위한 코드
				WebClient webClient = WebClient.builder().defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
				log.error("####### :: board webClient Start :: #######");
				Mono<List> userNames = webClient
						.post()   
						.uri(URL)  //요청 URL
						.contentType(MediaType.APPLICATION_JSON)
						.body(list, List.class)
						.retrieve()
						.bodyToMono(List.class);
				
				Mono<Map<Object, Object>> monoMapper = Mono.just(returnMap);
				log.error("####### :: board webClient End :: #######");
				return Mono.zip(list, totalCnt, userNames).flatMap(dd -> {
					List<Board> boardList = dd.getT1();
					List<Map<Object, Object>> userList = dd.getT3();
					
					for (int i = 0; i < boardList.size(); i++) {
						for (int j = 0; j < userList.size(); j++) {
							// 유저이름
							if(boardList.get(i).getUserId() != null) {
								if(boardList.get(i).getUserId().equals(userList.get(j).get("userId"))) {
									boardList.get(i).setUserNm(userList.get(j).get("userName").toString());
									break;
								}
								// 유저 미등록시
								if(j == userList.size() -1) {
									boardList.get(i).setUserNm("미등록 유저");
								}
							}
							else {
								boardList.get(i).setUserNm("미등록 유저");
							}
						}
					}
					
					returnMap.put("list", boardList);
					returnMap.put("totalCnt", dd.getT2());
					
					return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromProducer(monoMapper, Map.class));
				})
				.onErrorResume(err -> {
					err.printStackTrace();
					return Mono.error(err);
				});
			}
			// 게시판 상세보기
			else if(viewType.equals("detailView")){
				Mono<Board> listOne = boardRepository.findById(s.get("boardIdx").toString());
				Mono<List<BoardFile>> fileList = boardFileRepository.findByBoardIdx(s.get("boardIdx").toString()).collectList();
				// [::::::::::: webclient ::::::::::::]
				WebClient webClient = WebClient.builder().defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
				log.error("####### :: board webClient Start :: #######");
				
				String URL = totalURL + "/user/list/userNmOne";
				Mono<List> userNames = webClient
						.post()   
						.uri(URL)  //요청 URL
						.contentType(MediaType.APPLICATION_JSON)
						.body(listOne, Board.class)
						.retrieve()
						.bodyToMono(List.class);
				
				log.error("####### :: board webClient End :: #######");
				Mono<Map<Object, Object>> monoMapper = Mono.just(returnMap);
				
				return Mono.zip(listOne, userNames, fileList).flatMap(dd -> {
					Board one = dd.getT1();
					List<Map<Object, Object>> userList = dd.getT2();
					if(userList.size() > 0 && userList.get(0).get("userName") != null) {
						one.setUserNm(userList.get(0).get("userName").toString());
					}
					else {
						one.setUserNm("미등록 유저");
					}
					returnMap.put("one", one);
					returnMap.put("fileList", dd.getT3());
					
					return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromProducer(monoMapper, Map.class));
				})
				.onErrorResume(err -> {
					err.printStackTrace();
					return Mono.error(err);
				});
			}
			else {
				return ServerResponse.ok().body(Mono.just("fail"), String.class);
			}
			
		});
	}
	
	// board save
	@Transactional
	@CircuitBreaker(name = BOARD_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	public Mono<ServerResponse> boardAdd(ServerRequest request) {
		Mono<Board> acceptData = request.bodyToMono(Board.class);
		return acceptData.flatMap(s -> {
			try {
				// board insert
				// repository insert는 pk값이 null이어야 insert를 하지만 uuid는 자동으로 생성안되므로 수기로 insert해야됨 (2021-10-18)
				log.error("** 들어온 boardIdx : " + s.getBoardIdx());
				String boardIdx = UUIDUtil.createUUID();
				if(s.getBoardIdx() != null && !s.getBoardIdx().equals("")) {
					boardIdx = s.getBoardIdx();
				}
				Board board = Board.builder()
				.boardIdx(boardIdx)
				.boardType(s.getBoardType())
				.title(s.getTitle())
				.userId(s.getUserId())
				.contents(s.getContents())
				.build();
				boardRepository.saveBoard(board).subscribe();
				return ServerResponse.ok().body(Mono.just("success"), String.class);
			} catch (Exception e) {
				log.error("** board save fail : " + e.getMessage());
				return Mono.error(e);
			}
		})
		.onErrorResume(err -> {
			err.printStackTrace();
			return Mono.error(err);
		});
	}
	
	// board update
	@Transactional
	public Mono<ServerResponse> boardChg(ServerRequest request) {
		Mono<Board> acceptData = request.bodyToMono(Board.class);
		return acceptData.flatMap(s -> {
			try {
				// board update
				// Board class 의 컬럼값이 모두 적용이되므로 reg_dt, mod_dt가 update됨. 때문에 save 사용불가
				Board board = Board.builder()
				.boardIdx(s.getBoardIdx())
				.title(s.getTitle())
				.userId(s.getUserId())
				.contents(s.getContents())
				.build();
				boardRepository.updateBoard(board).subscribe();
				if(s.getFileDelList().size() > 0) {
					for (int i = 0; i < s.getFileDelList().size(); i++) {
						boardFileRepository.updateOneDelBoardFile(s.getFileDelList().get(i)).subscribe();
					}
				}
				return ServerResponse.ok().body(Mono.just("success"), String.class);
			} catch (Exception e) {
				log.error("** board update fail : " + e.getMessage());
				return Mono.error(e);
			}
		})
		.onErrorResume(err -> {
			err.printStackTrace();
			return Mono.error(err);
		});
	}
	
	// board delete
	public Mono<ServerResponse> boardDel(ServerRequest request) {
		Mono<Board> acceptData = request.bodyToMono(Board.class);
		return acceptData.flatMap(s -> {
			// board delete
			try {
				// delete
//				boardRepository.deleteById(s.getBoardIdx()).subscribe();
				
				// delyn y로 변경
				Board board = Board.builder()
							.boardIdx(s.getBoardIdx())
							.delYn("Y")
							.build();
				// 파일도 삭제
				BoardFile boardFile = BoardFile.builder()
									  .boardIdx(s.getBoardIdx())
									  .delYn("Y")
									  .build();
				boardRepository.updateDelBoard(board).subscribe();
				boardFileRepository.updateAllDelBoardFile(boardFile).subscribe();
				return ServerResponse.ok().body(Mono.just("success"), String.class);
			} catch (Exception e) {
				log.error("** board delete fail : " + e.getMessage());
				return Mono.error(e);
			}
		});
	}
	
	// circuitbreaker fallback
	public Mono<ServerResponse> fallback(ServerRequest request, Throwable t) {
        log.error("Fallback : " + t.getMessage());
        return ServerResponse.ok().body(Mono.just("error"), String.class);
    }
}
	
