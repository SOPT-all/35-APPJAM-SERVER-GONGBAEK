package com.ggang.be.api.group.everyGroup.service;

import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupDto;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupVo;
import com.ggang.be.domain.group.everyGroup.dto.ReadEveryGroup;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.timslot.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;

public interface EveryGroupService {
    EveryGroupDto getEveryGroupDetail(final long groupId, UserEntity userEntity);

    long getEveryGroupRegisterUserId(final long groupId);

    ReadEveryGroup getMyRegisteredGroups(UserEntity currentUser, boolean status);

    EveryGroupEntity findEveryGroupEntityByGroupId(long groupId);

    void writeCommentInGroup(CommentEntity commentEntity, final long groupId);

    ReadCommentGroup readCommentInGroup(UserEntity userEntity, boolean commentEntity, final long groupId);

    EveryGroupEntity registerEveryGroup(RegisterGroupServiceRequest serviceRequest,
                                        GongbaekTimeSlotEntity gongbaekTimeSlotEntity);

    void deleteEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity);

    ReadEveryGroup getActiveEveryGroups(UserEntity currentUser, Category category);

    void validateApplyEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity);

    boolean validateCancelEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity);

    void updateStatus();

    boolean isSameSchoolEveryGroup(UserEntity currentUser, EveryGroupVo groupVo);
}
