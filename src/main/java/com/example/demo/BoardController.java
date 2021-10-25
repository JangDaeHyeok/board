//package com.example.demo;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import reactor.core.publisher.Mono;
//
//@RestController
//public class BoardController {
//	
//	@Autowired
//	BoardService boardService;
//
//	@RequestMapping(value = { "/board/list2"}, method = RequestMethod.GET)
//    public Mono<ServerResponse> boardList2(@RequestParam Map<Object, Object> eMap) {
//		return boardService.testService(eMap);
//	}
//}
