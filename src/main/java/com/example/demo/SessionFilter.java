//package com.example.demo;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import org.springframework.web.util.pattern.PathPattern;
//import org.springframework.web.util.pattern.PathPatternParser;
//
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component
//public class SessionFilter implements WebFilter{
//	
//	private PathPattern basePattern;
//
//    private List<PathPattern> excludePatterns;
//
//    public SessionFilter() {
//        basePattern = new PathPatternParser()
//                            .parse("/board/**");
//        excludePatterns = new ArrayList<>();
////        excludePatterns.add(new PathPatternParser().parse("/api/ping/**"));
//    }
//	
//	@Override
//	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {
//		ServerHttpRequest request = serverWebExchange.getRequest();
//        log.info("##{} : {} {}", request.getHeaders().getFirst("X-Forwarded-For") == null ? request.getRemoteAddress() : request.getHeaders().getFirst("X-Forwarded-For"), request.getMethodValue(), request.getURI().toString());
//        // header에서 세션체크 (존재시 key값으로 redis에서 조회, 미존재시 로그인필요)
//        if (basePattern.matches(request.getPath().pathWithinApplication())
//            && !excludePatterns.stream()
//                               .anyMatch(pathPattern -> pathPattern.matches(request.getPath().pathWithinApplication()))
//           ) {
//            return serverWebExchange.getSession()
//            .doOnNext(session -> Optional.ofNullable(session.getAttribute("userId"))
//                                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "logout"))
//            )
//            .then(webFilterChain.filter(serverWebExchange));
//        } else {
//            return webFilterChain.filter(serverWebExchange);
//        }
//	}
//
//}
