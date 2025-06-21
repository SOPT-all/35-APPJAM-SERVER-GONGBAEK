package com.ggang.be.api.report.service;

import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.report.ReportEntity;

public interface ReportService {
	ReportEntity reportComment(long commentId, long reportId, long reportedId);
	ReportEntity reportGroup(long groupId, long reportId, long reportedId, GroupType groupType);
}
