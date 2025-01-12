package com.ggang.be.domain.group.onceGroup.application;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.group.GroupCommentVoMaker;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupDto;
import com.ggang.be.domain.group.onceGroup.infra.OnceGroupRepository;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.user.UserEntity;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OnceGroupServiceImpl implements OnceGroupService {

    private final OnceGroupRepository onceGroupRepository;
    private final GroupCommentVoMaker groupCommentVoMaker;

    @Override
    public OnceGroupDto getOnceGroupDetail(final long groupId, final UserEntity userEntity) {
        OnceGroupEntity onceGroupEntity = findIdOrThrow(groupId);
        return OnceGroupDto.toDto(onceGroupEntity, userEntity);
    }

    @Override
    public long getOnceGroupRegisterUserId(final long groupId) {
        OnceGroupEntity entity = findIdOrThrow(groupId);
        return entity.getUserEntity().getId();
    }

    @Override
    public OnceGroupEntity findOnceGroupEntityByGroupId(long groupId) {
        return onceGroupRepository.findById(groupId).orElseThrow(
            () -> new GongBaekException(ResponseError.GROUP_NOT_FOUND)
        );
    }

    @Override
    @Transactional
    public void writeCommentInGroup(CommentEntity commentEntity, final long groupId) {
        onceGroupRepository.findById(groupId)
            .orElseThrow(() -> new GongBaekException(ResponseError.GROUP_NOT_FOUND))
            .addComment(commentEntity);
        log.info("onceGroupEntity add Comment success CommentId was  : {}", commentEntity.getId());
    }

    @Override
    public ReadCommentGroup readCommentInGroup(boolean isPublic, long groupId) {

        OnceGroupEntity onceGroupEntity = onceGroupRepository.findById(groupId)
            .orElseThrow(() -> new GongBaekException(ResponseError.GROUP_NOT_FOUND));

        List<CommentEntity> commentEntities = onceGroupEntity
            .getComments().stream().filter(c -> c.isPublic() == isPublic).toList();

        int commentCount = commentEntities.size();

        List<GroupCommentVo> onceGroupCommentVos = groupCommentVoMaker.makeByOnceGroup(
            commentEntities, onceGroupEntity);

        return ReadCommentGroup.of(commentCount, onceGroupCommentVos);
    }

    @Override
    @Transactional
    public Long registerOnceGroup(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {

        OnceGroupEntity buildEntity = buildOnceGroupEntity(
            serviceRequest, gongbaekTimeSlotEntity);

        return onceGroupRepository.save(buildEntity).getId();
    }

    @Override
    public void isExistInOnceGroupTimeSlot(RegisterGroupServiceRequest serviceRequest) {

        if(onceGroupRepository.isInTime
            (serviceRequest.userEntity(), serviceRequest.startTime(), serviceRequest.endTime(),
                serviceRequest.weekDate(), Status.CLOSED))
            throw new GongBaekException(ResponseError.GROUP_ALREADY_EXIST);
    }

    private OnceGroupEntity buildOnceGroupEntity(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {
        return OnceGroupEntity.builder()
            .groupDate(serviceRequest.weekDate())
            .dueDate(serviceRequest.weekDate())
            .category(serviceRequest.category())
            .coverImg(serviceRequest.coverImg())
            .location(serviceRequest.location())
            .currentPeopleCount(1)
            .status(Status.RECRUITING)
            .maxPeopleCount(serviceRequest.maxPeopleCount())
            .title(serviceRequest.groupTitle())
            .gongbaekTimeSlotEntity(gongbaekTimeSlotEntity)
            .introduction(serviceRequest.introduction())
            .userEntity(serviceRequest.userEntity())
            .build();
    }

    private OnceGroupEntity findIdOrThrow(final long groupId) {
        return onceGroupRepository.findById(groupId).orElseThrow(
            () -> new GongBaekException(ResponseError.GROUP_NOT_FOUND)
        );
    }
}
