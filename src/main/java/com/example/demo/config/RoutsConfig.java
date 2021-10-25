package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.handler.BoardFileHandler;
import com.example.demo.handler.BoardHandler;

@Configuration
public class RoutsConfig {
	
	@Bean
    public RouterFunction<ServerResponse> boardRouter(BoardHandler handler) {
        return RouterFunctions.route()
                .POST("/board/list", handler::boardList)
                .POST("/board/save", handler::boardAdd)
                .PATCH("/board/update", handler::boardChg)
                .DELETE("/board/delete", handler::boardDel)
                .build(); 
    }
	
	@Bean
	public RouterFunction<ServerResponse> boardFileRouter(BoardFileHandler handler) {
		return RouterFunctions.route()
				.POST("/board/file/save", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), handler::boardFileAdd)
				.build(); 
	}
	
}
