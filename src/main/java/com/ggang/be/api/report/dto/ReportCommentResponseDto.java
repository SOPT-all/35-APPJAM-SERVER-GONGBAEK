package com.ggang.be.api.report.dto;

public record ReportCommentResponseDto(String message) {
	private static final String REPORT_SUCCESS_MESSAGE = "댓글 신고에 성공하였습니다.";

	public static ReportCommentResponseDto ok() {
		return new ReportCommentResponseDto(REPORT_SUCCESS_MESSAGE);
	}
}
