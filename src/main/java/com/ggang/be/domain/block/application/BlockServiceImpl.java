package com.ggang.be.domain.block.application;

import java.util.List;

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


	public List<String> findUserBlocks(long userId){
		return blockRepository.findUserId(userId)
			.stream()
			.map(UserEntity::getNickname)
			.toList();
	}

	private static BlockEntity buildBlockEntity(ReportEntity reportEntity, UserEntity userEntity) {
		return BlockEntity.builder()
			.report(reportEntity)
			.user(userEntity)
			.build();
	}
}
