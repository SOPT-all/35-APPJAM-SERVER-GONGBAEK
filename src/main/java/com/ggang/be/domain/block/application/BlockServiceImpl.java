package com.ggang.be.domain.block.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ggang.be.domain.block.BlockEntity;
import com.ggang.be.domain.block.infra.BlockRepository;
import com.ggang.be.domain.report.ReportEntity;
import com.ggang.be.domain.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockServiceImpl {

	private final BlockRepository blockRepository;

	@Transactional
	public void blockUser(ReportEntity reportEntity, UserEntity userEntity) {
		blockRepository.save(buildBlockEntity(reportEntity, userEntity));
	}

	private static BlockEntity buildBlockEntity(ReportEntity reportEntity, UserEntity userEntity) {
		return BlockEntity.builder()
			.report(reportEntity)
			.user(userEntity)
			.build();
	}
}
