package com.ggang.be.api.group.registry;

import java.util.List;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.global.annotation.Registry;

import lombok.RequiredArgsConstructor;

@Registry
@RequiredArgsConstructor
public class FindGroupCreatorStrategyRegistry {
	private final List<FindGroupCreatorStrategy> strategies;

	public FindGroupCreatorStrategy findGroupCreatorStrategy(GroupType groupType) {
		return strategies.stream()
			.filter(strategy -> strategy.support(groupType))
			.findFirst()
			.orElseThrow(() -> new GongBaekException(ResponseError.BAD_REQUEST));
	}

}
