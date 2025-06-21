package com.ggang.be.api.group.registry;

import com.ggang.be.api.group.dto.GroupCreatorVo;
import com.ggang.be.domain.constant.GroupType;

public interface FindGroupCreatorStrategy {

	boolean support(GroupType groupType);

	GroupCreatorVo findCreator(long groupId);
}
