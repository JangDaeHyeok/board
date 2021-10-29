package com.example.demo.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("board_tb")
public class Board{

	@Id
	private String boardIdx;		// 게시판 idx
	private String title;			// 게시판 제목
	private String boardType;		// 게시판 타입
	private String userId;			// 게시판 작성자 id
	private String contents;		// 게시판 내용
	private String delYn;			// 삭제유무
	private LocalDateTime regDt;	// 등록일
	private LocalDateTime modDt;	// 수정일
	
	@Transient	// crud 무시
	private String userNm;
	
	@Transient	// crud 무시
	private String searchType;		// 검색조건
	@Transient	// crud 무시
	private String searchKeyword;	// 검색어
	
	
	@Transient	// crud 무시
	private List<String> fileDelList = new ArrayList<>();		// 파일 삭제 리스트
	
	
	@Builder
	public Board(String boardIdx, String title, String boardType, String userId, String contents, String delYn, String searchType, String searchKeyword) {
		this.boardIdx = boardIdx;
		this.boardType = boardType;
		this.title = title;
		this.userId = userId;
		this.contents = contents;
		this.delYn = delYn;
		this.searchType = searchType;
		this.searchKeyword = searchKeyword;
	}

}
