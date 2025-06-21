package com.ggang.be.api.report.dto;

public record ReportGroupResponse(String message) {

	private static final String REPORT_GROUP_SUCCESS_MESSAGE = "모임 신고에 성공하였습니다.";

	public static ReportGroupResponse create() {
		return new ReportGroupResponse(REPORT_GROUP_SUCCESS_MESSAGE);
	}
}
