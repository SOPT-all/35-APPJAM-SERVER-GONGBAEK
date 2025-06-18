package com.ggang.be.domain.report;

import com.ggang.be.domain.BaseTimeEntity;
import com.ggang.be.domain.constant.ReportType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReportEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	private Long targetId;

	@Enumerated(EnumType.STRING)
	private ReportType targetType;

	private Long reportUserId; // 신고자

	private Long reportedUserId; // 신고 당한 사람



	@Builder
	public ReportEntity(Long targetId, ReportType targetType, Long reportUserId, Long reportedUserId) {
		this.targetId = targetId;
		this.targetType = targetType;
		this.reportUserId = reportUserId;
		this.reportedUserId = reportedUserId;
	}


}
