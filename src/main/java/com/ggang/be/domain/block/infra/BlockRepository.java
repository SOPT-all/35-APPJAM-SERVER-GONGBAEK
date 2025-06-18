package com.ggang.be.domain.block.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ggang.be.domain.block.BlockEntity;

public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
}
