package com.example.demo.board.file;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.board.Board;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoardFileRepository extends ReactiveCrudRepository<BoardFile, String>{
	@Query("SELECT * FROM BOARD_FILE_TB WHERE BOARD_IDX = :boardIdx AND DEL_YN = 'N'")
	Flux<BoardFile> findByBoardIdx(String boardIdx);
	
	@Query("INSERT INTO board_file_tb (board_file_idx, board_idx, file_nm, origin_file_nm, file_path, reg_dt) VALUES (:#{#boardFile.boardFileIdx}, :#{#boardFile.boardIdx}, :#{#boardFile.fileNm}, :#{#boardFile.originFileNm}, :#{#boardFile.filePath}, now())")
	Mono<Integer> saveBoardFile(@Param("boardFile") BoardFile boardFile);
	
	@Query("UPDATE board_file_tb SET del_yn = :#{#boardFile.delYn}, mod_dt = now() WHERE board_file_tb.board_idx = :#{#boardFile.boardIdx}")
	Mono<Integer> updateAllDelBoardFile(@Param("boardFile") BoardFile boardFile);
	
	@Query("UPDATE board_file_tb SET del_yn = 'Y', mod_dt = now() WHERE board_file_tb.board_file_idx = :boardFileIdx")
	Mono<Integer> updateOneDelBoardFile(String boardFileIdx);
}
