package com.ggang.be.api.group.onceGroup.service;

import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupDto;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.group.vo.ReadCommentGroup;

public interface OnceGroupService {
    OnceGroupDto getOnceGroupDetail(final long groupId, UserEntity user);
    long getOnceGroupRegisterUserId(final long groupId);
    OnceGroupEntity findOnceGroupEntityByGroupId(long groupId);

    void writeCommentInGroup(CommentEntity commentEntity, final long groupId);

    ReadCommentGroup readCommentInGroup(boolean isPublic, final long groupId);

    Long registerOnceGroup(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity);

    void isExistInOnceGroupTimeSlot(RegisterGroupServiceRequest serviceRequest);

}
