package com.example.demo.board;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoardRepository extends ReactiveCrudRepository<Board, String>{
	
	@Query("SELECT * FROM BOARD_TB "
			+ "WHERE BOARD_TYPE = :#{#board.boardType} "
			+ "AND DEL_YN = 'N' "
			+ "AND (:#{#board.searchType} is null or "
			+ 	"if(:#{#board.searchType} = 'title', title like CONCAT('%',:#{#board.searchKeyword},'%'), "
			+ 		"if(:#{#board.searchType} = 'contents', contents like CONCAT('%',:#{#board.searchKeyword},'%'), "
			+ 			"(title like CONCAT('%',:#{#board.searchKeyword},'%') or contents like CONCAT('%',:#{#board.searchKeyword},'%'))"
			+ 		")"
			+ 	")"
			+ ")"
			+ "ORDER BY REG_DT DESC LIMIT :#{#paging}, :#{#size}")
	Flux<Board> findAllPaging(@Param("board") Board board, @Param("paging") int paging, @Param("size") int size);
	
	@Query("SELECT count(*) FROM BOARD_TB "
			+ "WHERE BOARD_TYPE = :#{#board.boardType} "
			+ "AND DEL_YN = 'N'"
			+ "AND (:#{#board.searchType} is null or "
			+ 	"if(:#{#board.searchType} = 'title', title like CONCAT('%',:#{#board.searchKeyword},'%'), "
			+ 		"if(:#{#board.searchType} = 'contents', contents like CONCAT('%',:#{#board.searchKeyword},'%'), "
			+ 			"(title like CONCAT('%',:#{#board.searchKeyword},'%') or contents like CONCAT('%',:#{#board.searchKeyword},'%'))"
			+ 		")"
			+ 	")"
			+ ")")
	Mono<Integer> findTotalCount(@Param("board") Board board);
	
	@Query("INSERT INTO board_tb (board_idx, board_type, title, user_id, contents, reg_dt) VALUES (:#{#board.boardIdx}, :#{#board.boardType}, :#{#board.title}, :#{#board.userId}, :#{#board.contents}, now())")
	Mono<Integer> saveBoard(@Param("board") Board board);
	
	@Query("UPDATE board_tb SET title = :#{#board.title}, user_id = :#{#board.userId}, contents = :#{#board.contents}, mod_dt = now() WHERE board_tb.board_idx = :#{#board.boardIdx}")
	Mono<Integer> updateBoard(@Param("board") Board board);
	
	@Query("UPDATE board_tb SET del_yn = :#{#board.delYn}, mod_dt = now() WHERE board_tb.board_idx = :#{#board.boardIdx}")
	Mono<Integer> updateDelBoard(@Param("board") Board board);
	
}
