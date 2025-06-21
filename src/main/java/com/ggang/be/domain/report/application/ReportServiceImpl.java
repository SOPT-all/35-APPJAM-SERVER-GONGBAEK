package com.ggang.be.domain.report.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ggang.be.api.report.service.ReportService;
import com.ggang.be.domain.constant.GroupType;
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
	public ReportEntity reportComment(long commentId, long userId, long reportedId) {
		return reportRepository
			.save(buildReport(commentId, userId, reportedId, ReportType.COMMENT));
	}

	@Override
	@Transactional
	public ReportEntity reportGroup(long groupId, long userId, long reportedId, GroupType groupType) {
		if(groupType == GroupType.ONCE)
			return reportRepository
				.save(buildReport(groupId, userId, reportedId, ReportType.ONCE_GROUP));

		return reportRepository.save(buildReport(groupId, userId, reportedId, ReportType.WEEKLY_GROUP));

	}

	private ReportEntity buildReport(long targetId, long userId, long reportedId, ReportType groupType) {
		return ReportEntity.builder()
			.targetId(targetId)
			.targetType(groupType)
			.reportUserId(userId)
			.reportedUserId(reportedId)
			.build();
	}
}
