package com.ggang.be.domain.group.onceGroup.application;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.group.GroupStatusUpdater;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.group.GroupCommentVoMaker;
import com.ggang.be.domain.group.GroupVoMaker;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupDto;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupVo;
import com.ggang.be.domain.group.onceGroup.dto.ReadOnceGroup;
import com.ggang.be.domain.group.onceGroup.infra.OnceGroupRepository;
import com.ggang.be.domain.group.onceGroup.vo.ReadOnceGroupCommentCommonVo;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.timslot.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OnceGroupServiceImpl implements OnceGroupService {

    private final OnceGroupRepository onceGroupRepository;
    private final GroupVoMaker groupVoMaker;
    private final GroupCommentVoMaker groupCommentVoMaker;
    private final GroupStatusUpdater groupStatusUpdater;

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
    public ReadOnceGroup getMyRegisteredGroups(UserEntity currentUser, boolean status) {
        List<OnceGroupEntity> onceGroupEntities = onceGroupRepository.findByUserEntity_Id(currentUser.getId());

        return ReadOnceGroup.of(groupVoMaker.makeOnceGroup(getGroupsByStatus(onceGroupEntities, status)));
    }

    @Override
    public ReadOnceGroup getActiveOnceGroups(UserEntity currentUser, Category category) {
        List<OnceGroupEntity> onceGroupEntities;
        if(category == null) onceGroupEntities = onceGroupRepository.findAll();
        else onceGroupEntities = onceGroupRepository.findAllByCategory(category);

        return ReadOnceGroup.of(groupVoMaker.makeOnceGroup(getRecruitingGroups(onceGroupEntities)));
    }

    private List<OnceGroupEntity> getRecruitingGroups(List<OnceGroupEntity> onceGroupEntities) {
        return onceGroupEntities.stream()
                .filter(group -> group.getStatus().isRecruiting())
                .collect(Collectors.toList());
    }

    private List<OnceGroupEntity> getGroupsByStatus(List<OnceGroupEntity> onceGroupEntities, boolean status){
        if (status) {
            return onceGroupEntities.stream()
                    .filter(group -> group.getStatus().isActive())
                    .collect(Collectors.toList());
        } else {
            return onceGroupEntities.stream()
                    .filter(group -> group.getStatus().isClosed())
                    .collect(Collectors.toList());
        }
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
    public ReadCommentGroup readCommentInGroup(UserEntity userEntity, boolean isPublic, long groupId) {

        OnceGroupEntity onceGroupEntity = onceGroupRepository.findById(groupId)
                .orElseThrow(() -> new GongBaekException(ResponseError.GROUP_NOT_FOUND));

        List<CommentEntity> commentEntities = onceGroupEntity
                .getComments().stream().filter(c -> c.isPublic() == isPublic).toList();
        int commentCount = commentEntities.size();

        List<GroupCommentVo> onceGroupCommentVos = groupCommentVoMaker.makeByOnceGroup(userEntity,
                commentEntities, onceGroupEntity);
        ReadOnceGroupCommentCommonVo vo = ReadOnceGroupCommentCommonVo.of(
            commentCount, groupId, onceGroupEntity.getStatus());

        return ReadCommentGroup.fromOnceGroup(vo, onceGroupCommentVos);
    }

    @Override
    @Transactional
    public OnceGroupEntity registerOnceGroup(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {

        OnceGroupEntity buildEntity = buildOnceGroupEntity(
            serviceRequest, gongbaekTimeSlotEntity);

        return onceGroupRepository.save(buildEntity);
    }

    @Override
    @Transactional
    public void deleteOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity) {
        validateDeleteOnceGroup(currentUser, onceGroupEntity);
        onceGroupRepository.delete(onceGroupEntity);
    }

    @Override
    @Transactional
    public void validateApplyOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity){
        validateAlreadyApplied(currentUser, onceGroupEntity);
        validateHostAccess(currentUser, onceGroupEntity);
        validateGroupFull(onceGroupEntity);
    }

    @Override
    @Transactional
    public boolean validateCancelOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity){
        if(onceGroupEntity.isHost(currentUser))
            throw new GongBaekException(ResponseError.UNAUTHORIZED_ACCESS);

        return onceGroupEntity.isApply(currentUser);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateStatus() {
        List<OnceGroupEntity> onceGroupEntities = onceGroupRepository.findAllByNotStatus(Status.CLOSED);
        onceGroupEntities
            .forEach(groupStatusUpdater::updateOnceGroup);
    }

    @Override
    public boolean isSameSchoolOnceGroup(UserEntity currentUser, OnceGroupVo groupVo) {
        String userSchool = currentUser.getSchool().getSchoolName();
        OnceGroupEntity onceGroupEntity = findOnceGroupEntityByGroupId(
            groupVo.groupId());
        String groupCreatorSchool = onceGroupEntity.getUserEntity().getSchool().getSchoolName();

        return userSchool.equals(groupCreatorSchool);
    }

    private void validateDeleteOnceGroup(UserEntity currentUser, OnceGroupEntity onceGroupEntity) {
        if (!onceGroupEntity.isHost(currentUser))
            throw new GongBaekException(ResponseError.UNAUTHORIZED_ACCESS);
    }

    private OnceGroupEntity buildOnceGroupEntity(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {
        return OnceGroupEntity.builder()
            .groupDate(serviceRequest.weekDate())
            .category(serviceRequest.category())
            .coverImg(serviceRequest.coverImg())
            .location(serviceRequest.location())
            .status(Status.RECRUITING)
            .maxPeopleCount(serviceRequest.maxPeopleCount())
            .title(serviceRequest.groupTitle())
            .gongbaekTimeSlotEntity(gongbaekTimeSlotEntity)
            .introduction(serviceRequest.introduction())
            .userEntity(serviceRequest.userEntity())
            .build();
    }

    private void validateAlreadyApplied(UserEntity currentUser, OnceGroupEntity onceGroupEntity) {
        if(onceGroupEntity.isApply(currentUser)) {
            throw new GongBaekException(ResponseError.APPLY_ALREADY_EXIST);
        }
    }

    private void validateHostAccess(UserEntity currentUser, OnceGroupEntity onceGroupEntity) {
        if(onceGroupEntity.isHost(currentUser)) {
            throw new GongBaekException(ResponseError.UNAUTHORIZED_ACCESS);
        }
    }

    private void validateGroupFull(OnceGroupEntity onceGroupEntity) {
        if(onceGroupEntity.getCurrentPeopleCount() == onceGroupEntity.getMaxPeopleCount()) {
            throw new GongBaekException(ResponseError.GROUP_ALREADY_FULL);
        }
    }

    private OnceGroupEntity findIdOrThrow(final long groupId) {
        return onceGroupRepository.findById(groupId).orElseThrow(
            () -> new GongBaekException(ResponseError.GROUP_NOT_FOUND)
        );
    }
}
