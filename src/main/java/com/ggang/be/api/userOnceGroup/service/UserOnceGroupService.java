package com.ggang.be.api.userOnceGroup.service;

import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.group.onceGroup.dto.ReadOnceGroup;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.userOnceGroup.UserOnceGroupEntity;

import java.util.List;

public interface UserOnceGroupService {
    ReadOnceGroup getMyAppliedGroups(UserEntity currentUser, boolean status);

    List<OnceGroupEntity> getGroupsByStatus(List<UserOnceGroupEntity> userOnceGroupEntities, boolean status);

}
