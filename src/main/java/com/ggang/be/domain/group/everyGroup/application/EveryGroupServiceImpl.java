package com.ggang.be.domain.group.everyGroup.application;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.group.everyGroup.service.EveryGroupService;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.group.GroupCommentVoMaker;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupDto;
import com.ggang.be.domain.group.everyGroup.infra.EveryGroupRepository;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.user.UserEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EveryGroupServiceImpl implements EveryGroupService {

    private final EveryGroupRepository everyGroupRepository;
    private final GroupCommentVoMaker groupCommentVoMaker;

    @Override
    public EveryGroupDto getEveryGroupDetail(final long groupId, final UserEntity userEntity) {
        EveryGroupEntity entity = findIdOrThrow(groupId);
        return EveryGroupDto.toDto(entity, userEntity);
    }

    @Override
    public long getEveryGroupRegisterUserId(final long groupId) {
        EveryGroupEntity entity = findIdOrThrow(groupId);
        return entity.getUserEntity().getId();
    }

    @Override
    public EveryGroupEntity findEveryGroupEntityByGroupId(long groupId) {
        return everyGroupRepository.findById(groupId).orElseThrow(
            () -> new GongBaekException(ResponseError.GROUP_NOT_FOUND)
        );
    }

    @Override
    @Transactional
    public void writeCommentInGroup(CommentEntity commentEntity, final long groupId) {
        everyGroupRepository.findById(groupId)
            .orElseThrow(() -> new GongBaekException(ResponseError.GROUP_NOT_FOUND))
            .addComment(commentEntity);
        log.info("everyGroupEntity add Comment success CommentId was  : {}", commentEntity.getId());
    }

    @Override
    public ReadCommentGroup readCommentInGroup(boolean isPublic, long groupId) {

        EveryGroupEntity everyGroupEntity = everyGroupRepository.findById(groupId)
            .orElseThrow(() -> new GongBaekException(ResponseError.GROUP_NOT_FOUND));

        List<CommentEntity> commentEntities = everyGroupEntity
            .getComments().stream().filter(c -> c.isPublic() == isPublic).toList();

        int commentCount = commentEntities.size();

        List<GroupCommentVo> onceGroupCommentVos = groupCommentVoMaker.makeByEveryGroup(
            commentEntities, everyGroupEntity);

        return ReadCommentGroup.of(commentCount, onceGroupCommentVos);

    }

    @Override
    @Transactional
    public Long registerEveryGroup(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {
        isExistedInTime(serviceRequest);

        EveryGroupEntity buildEntity = buildEveryGroupEntity(
            serviceRequest, gongbaekTimeSlotEntity);

        return everyGroupRepository.save(buildEntity).getId();
    }

    private EveryGroupEntity buildEveryGroupEntity(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {
        return EveryGroupEntity.builder()
            .dueDate(serviceRequest.dueDate())
            .category(serviceRequest.category())
            .coverImg(serviceRequest.coverImg())
            .location(serviceRequest.location())
            .status(Status.RECRUITING)
            .currentPeopleCount(1)
            .maxPeopleCount(serviceRequest.maxPeopleCount())
            .gongbaekTimeSlotEntity(gongbaekTimeSlotEntity)
            .title(serviceRequest.groupTitle())
            .introduction(serviceRequest.introduction())
            .userEntity(serviceRequest.userEntity())
            .build();
    }

    @Override
    public void isExistedInTime(RegisterGroupServiceRequest serviceRequest) {
        if(everyGroupRepository.isInTime
            (serviceRequest.userEntity(), serviceRequest.startTime(), serviceRequest.endTime(),
                serviceRequest.weekDay(), Status.CLOSED))
            throw new GongBaekException(ResponseError.GROUP_ALREADY_EXIST);
    }


    private EveryGroupEntity findIdOrThrow(final long groupId) {
        return everyGroupRepository.findById(groupId).orElseThrow(
            () -> new GongBaekException(ResponseError.GROUP_NOT_FOUND)
        );
    }

}
