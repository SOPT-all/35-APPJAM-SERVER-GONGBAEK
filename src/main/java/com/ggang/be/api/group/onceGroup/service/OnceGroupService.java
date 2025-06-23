package com.ggang.be.api.group.onceGroup.service;

import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupDto;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupVo;
import com.ggang.be.domain.group.onceGroup.dto.ReadOnceGroup;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.timslot.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;

public interface OnceGroupService {
    OnceGroupDto getOnceGroupDetail(final long groupId, UserEntity user);

    long getOnceGroupRegisterUserId(final long groupId);

    ReadOnceGroup getMyRegisteredGroups(UserEntity currentUser, boolean status);

    OnceGroupEntity findOnceGroupEntityByGroupId(long groupId);

    void writeCommentInGroup(CommentEntity commentEntity, final long groupId);

    ReadCommentGroup readCommentInGroup(UserEntity userEntity, boolean isPublic, final long groupId);

    OnceGroupEntity registerOnceGroup(RegisterGroupServiceRequest serviceRequest,
                           GongbaekTimeSlotEntity gongbaekTimeSlotEntity);

    void deleteOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity);

    ReadOnceGroup getActiveOnceGroups(UserEntity currentUser, Category category);

    void validateApplyOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity);

    boolean validateCancelOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity);

    void updateStatus();

    boolean isSameSchoolOnceGroup(UserEntity currentUser, OnceGroupVo groupVo);
}
