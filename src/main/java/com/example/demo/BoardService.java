//package com.example.demo;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import com.example.demo.board.Board;
//import com.example.demo.board.BoardRepository;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Service
//public class BoardService {
//
//	@Autowired
//	BoardRepository boardRepository;
//	
//	public Mono<ServerResponse> testService(Map<Object, Object> eMap) {
//		Map<Object, Object> returnMap = new HashMap<>();
//		
//		// 페이징
//		int paging = 1;	// defalut page
//		if(eMap.get("paging") != null) {
//			paging = Integer.valueOf(eMap.get("paging").toString());
//		}
//		int size = 5;
//		Sort sort = Sort.by("reg_dt").descending();
//		Flux<Board> list = boardRepository.findAll(sort).skip(paging * size).take(size);
//		
//		List<String> userIdList = new ArrayList<>();
//		List<Board> boardList = new ArrayList<>();
//		list.subscribe(t -> {
//			userIdList.add(t.getUserId());
//			boardList.add(t);
//		});
//		
//		// [::::::::::: webclient ::::::::::::]
//		WebClient webClient = WebClient.builder().defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
//		String URL = "http://localhost:6118/user/list/userNm";	// 임시 user url
//		Mono<Map> userNames = webClient
//				.post()   
//				.uri(URL)  //요청 URL
//				.bodyValue(userIdList)
//				.retrieve()
//				.bodyToMono(Map.class);
//		
//		
//		return userNames.flatMap(response -> {
//			List<Map<String, String>> userList = (List<Map<String, String>>) response.get("list");
//			for (int i = 0; i < boardList.size(); i++) {
//				for (int j = 0; j < userList.size(); j++) {
//					// 유저이름
//					if(boardList.get(i).getUserId().equals(userList.get(j).get("userId"))) {
//						boardList.get(i).setUserNm(userList.get(j).get("userName"));
//						break;
//					}
//					// 유저 미등록시
//					if(j == userList.size() -1) {
//						boardList.get(i).setUserNm("미등록 유저");
//					}
//				}
//			}
//			System.out.println(boardList);
//			returnMap.put("list", boardList);
//			
//			Mono<Map<Object, Object>> monoMapper = Mono.just(returnMap);
//			
//			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//					.body(BodyInserters.fromProducer(monoMapper, HashMap.class));
//		});
//	}
//}
