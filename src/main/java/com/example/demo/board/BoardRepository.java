package com.example.demo.board;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoardRepository extends ReactiveCrudRepository<Board, String>{
	
	@Query("SELECT * FROM BOARD_TB WHERE BOARD_TYPE = :boardType ORDER BY REG_DT DESC LIMIT :paging, :size")
	Flux<Board> findAllPaging(String boardType, int paging, int size);
	
	@Query("SELECT count(*) FROM BOARD_TB WHERE BOARD_TYPE = :boardType")
	Mono<Integer> findTotalCount(String boardType);
	
	@Query("INSERT INTO board_tb (board_idx, board_type, title, user_id, contents, reg_dt) VALUES (:#{#board.boardIdx}, :#{#board.boardType}, :#{#board.title}, :#{#board.userId}, :#{#board.contents}, now())")
	Mono<Integer> saveBoard(@Param("board") Board board);
	
	@Query("UPDATE board_tb SET title = :#{#board.title}, user_id = :#{#board.userId}, contents = :#{#board.contents}, mod_dt = now() WHERE board_tb.board_idx = :#{#board.boardIdx}")
	Mono<Integer> updateBoard(@Param("board") Board board);
	
}
