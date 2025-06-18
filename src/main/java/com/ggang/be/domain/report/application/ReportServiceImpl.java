package com.ggang.be.domain.report.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ggang.be.api.report.service.ReportService;
import com.ggang.be.domain.constant.ReportType;
import com.ggang.be.domain.report.ReportEntity;
import com.ggang.be.domain.report.infra.ReportRepsitory;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final ReportRepsitory reportRepository;


	@Override
	@Transactional
	public ReportEntity reportComment(long commentId, long reportId, long reportedId) {
		return reportRepository
			.save(buildReport(commentId, reportId, reportedId, ReportType.COMMENT));
	}

	@Override
	@Transactional
	public ReportEntity reportWeeklyGroup(long groupId, long reportId, long reportedId) {
		return reportRepository
			.save(buildReport(groupId, reportId, reportedId, ReportType.WEEKLY_GROUP));
	}

	@Override
	@Transactional
	public ReportEntity reportOnceGroup(long groupId, long reportId, long reportedId) {
		return reportRepository
			.save(buildReport(groupId, reportId, reportedId, ReportType.ONCE_GROUP));
	}

	private ReportEntity buildReport(long targetId, long reportId, long reportedId, ReportType groupType) {
		return ReportEntity.builder()
			.targetId(targetId)
			.targetType(groupType)
			.reportUserId(reportId)
			.reportedUserId(reportedId)
			.build();
	}
}
