package com.ggang.be.domain.block;

import com.ggang.be.domain.BaseTimeEntity;
import com.ggang.be.domain.report.ReportEntity;
import com.ggang.be.domain.user.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity(name = "block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@OneToOne
	private UserEntity user;


	@ManyToOne
	private ReportEntity report;


	@Builder
	public BlockEntity(UserEntity user, ReportEntity report) {
		this.user = user;
		this.report = report;
	}

}
