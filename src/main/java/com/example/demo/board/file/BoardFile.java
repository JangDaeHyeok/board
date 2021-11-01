package com.example.demo.board.file;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("board_file_tb")
public class BoardFile {

	@Id
	private String boardFileIdx;		// 게시판 파일 idx
	private String boardIdx;			// 게시판 idx
	private String fileNm;				// 파일이름
	private String originFileNm;		// 기존 파일이름
	private String filePath;			// 파일 저장위치
	private String delYn;				// 삭제 유무
	private LocalDateTime regDt;		// 등록일

	@Builder
	public BoardFile(String boardFileIdx, String boardIdx, String fileNm, String originFileNm, String filePath, String delYn) {
		this.boardFileIdx = boardFileIdx;
		this.boardIdx = boardIdx;
		this.fileNm = fileNm;
		this.originFileNm = originFileNm;
		this.filePath = filePath;
		this.delYn = delYn;
	}
}
