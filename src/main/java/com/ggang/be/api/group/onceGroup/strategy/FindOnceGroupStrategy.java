package com.ggang.be.api.group.onceGroup.strategy;

import com.ggang.be.api.group.dto.GroupCreatorVo;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.api.group.registry.FindGroupCreatorStrategy;
import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.global.annotation.Strategy;

import lombok.RequiredArgsConstructor;

@Strategy
@RequiredArgsConstructor
public class FindOnceGroupStrategy implements FindGroupCreatorStrategy {

	private final OnceGroupService onceGroupService;

	@Override
	public boolean support(GroupType groupType) {
		return groupType == GroupType.ONCE;
	}

	@Override
	public GroupCreatorVo findCreator(long groupId) {
		OnceGroupEntity onceGroupEntityByGroupId = onceGroupService.findOnceGroupEntityByGroupId(groupId);
		UserEntity creator = onceGroupEntityByGroupId.getUserEntity();
		return GroupCreatorVo.from(creator.getId());
	}
}
