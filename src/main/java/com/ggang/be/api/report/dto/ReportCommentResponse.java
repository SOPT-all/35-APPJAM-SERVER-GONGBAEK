package com.ggang.be.api.report.dto;

public record ReportCommentResponse(String message) {
	private static final String REPORT_COMMENT_SUCCESS_MESSAGE = "댓글 신고에 성공하였습니다.";

	public static ReportCommentResponse create() {
		return new ReportCommentResponse(REPORT_COMMENT_SUCCESS_MESSAGE);
	}
}
