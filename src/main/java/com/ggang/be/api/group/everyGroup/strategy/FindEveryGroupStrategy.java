package com.ggang.be.api.group.everyGroup.strategy;

import com.ggang.be.api.group.dto.GroupCreatorVo;
import com.ggang.be.api.group.everyGroup.service.EveryGroupService;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.api.group.registry.FindGroupCreatorStrategy;
import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.global.annotation.Strategy;

import lombok.RequiredArgsConstructor;

@Strategy
@RequiredArgsConstructor
public class FindEveryGroupStrategy implements FindGroupCreatorStrategy {
	private final EveryGroupService everyGroupService;

	@Override
	public boolean support(GroupType groupType) {
		return groupType == GroupType.WEEKLY;
	}

	@Override
	public GroupCreatorVo findCreator(long groupId) {
		EveryGroupEntity everyGroupEntityByGroupId = everyGroupService.findEveryGroupEntityByGroupId(groupId);
		UserEntity creator = everyGroupEntityByGroupId.getUserEntity();
		return GroupCreatorVo.from(creator.getId());
	}
}
