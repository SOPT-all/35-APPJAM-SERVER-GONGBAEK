package com.ggang.be.api.group.everyGroup.service;

import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupDto;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.group.vo.ReadCommentGroup;

public interface EveryGroupService {
    EveryGroupDto getEveryGroupDetail(final long groupId, UserEntity userEntity);
    long getEveryGroupRegisterUserId(final long groupId);
    EveryGroupEntity findEveryGroupEntityByGroupId(long groupId);

    void writeCommentInGroup(CommentEntity commentEntity, final long groupId);

    ReadCommentGroup readCommentInGroup(boolean commentEntity, final long groupId);

    Long registerEveryGroup(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity);

    void isExistedInTime(RegisterGroupServiceRequest serviceRequest);
}
