package com.ggang.be.domain.group.everyGroup.application;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.group.GroupStatusUpdater;
import com.ggang.be.api.group.everyGroup.service.EveryGroupService;
import com.ggang.be.domain.block.application.BlockServiceImpl;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.group.GroupCommentVoMaker;
import com.ggang.be.domain.group.GroupVoMaker;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupDto;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupVo;
import com.ggang.be.domain.group.everyGroup.dto.ReadEveryGroup;
import com.ggang.be.domain.group.everyGroup.infra.EveryGroupRepository;
import com.ggang.be.domain.group.everyGroup.vo.ReadEveryGroupCommentCommonVo;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.timslot.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EveryGroupServiceImpl implements EveryGroupService {

    private final EveryGroupRepository everyGroupRepository;
    private final GroupVoMaker groupVoMaker;
    private final GroupCommentVoMaker groupCommentVoMaker;
    private final GroupStatusUpdater groupStatusUpdater;
    private final BlockServiceImpl blockService;

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
    public ReadEveryGroup getMyRegisteredGroups(UserEntity currentUser, boolean status) {
        List<EveryGroupEntity> everyGroupEntities = everyGroupRepository.findByUserEntity_Id(
            currentUser.getId());

        return ReadEveryGroup.of(
            groupVoMaker.makeEveryGroup(getGroupsByStatus(everyGroupEntities, status)));
    }

    @Override
    public ReadEveryGroup getActiveEveryGroups(UserEntity currentUser, Category category) {
        List<EveryGroupEntity> everyGroupEntities;
        if(category == null) everyGroupEntities = everyGroupRepository.findAll();
        else everyGroupEntities = everyGroupRepository.findAllByCategory(category);

        return ReadEveryGroup.of(
            groupVoMaker.makeEveryGroup(getRecruitingGroups(everyGroupEntities)));
    }

    private List<EveryGroupEntity> getRecruitingGroups(List<EveryGroupEntity> everyGroupEntities) {
        return everyGroupEntities.stream()
            .filter(group -> group.getStatus().isRecruiting())
            .collect(Collectors.toList());
    }

    private List<EveryGroupEntity> getGroupsByStatus(List<EveryGroupEntity> everyGroupEntities,
        boolean status) {
        if (status) {
            return everyGroupEntities.stream()
                .filter(group -> group.getStatus().isActive())
                .collect(Collectors.toList());
        } else {
            return everyGroupEntities.stream()
                .filter(group -> group.getStatus().isClosed())
                .collect(Collectors.toList());
        }
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
    public ReadCommentGroup readCommentInGroup(UserEntity userEntity, boolean isPublic,
        long groupId) {

        EveryGroupEntity everyGroupEntity = everyGroupRepository.findById(groupId)
            .orElseThrow(() -> new GongBaekException(ResponseError.GROUP_NOT_FOUND));

        List<String> blockNickNames = blockService.findUserBlocks(userEntity.getId());

        List<CommentEntity> commentEntities = everyGroupEntity
            .getComments().stream()
            .filter(c -> blockNickNames.contains(c.getUserEntity().getNickname()))
            .filter(c -> c.isPublic() == isPublic).toList();

        int commentCount = commentEntities.size();

        List<GroupCommentVo> everyGroupCommentVos = groupCommentVoMaker.makeByEveryGroup(
            userEntity, commentEntities, everyGroupEntity);

        ReadEveryGroupCommentCommonVo readEveryGroupCommentCommonVo = ReadEveryGroupCommentCommonVo.of(
            commentCount,
            groupId,
            everyGroupEntity.getStatus());

        return ReadCommentGroup.fromEveryGroup(readEveryGroupCommentCommonVo,
            everyGroupCommentVos);
    }


    @Override
    @Transactional
    public EveryGroupEntity registerEveryGroup(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {

        EveryGroupEntity buildEntity = buildEveryGroupEntity(
            serviceRequest, gongbaekTimeSlotEntity);

        return everyGroupRepository.save(buildEntity);
    }

    @Override
    @Transactional
    public void deleteEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity) {
        validateDeleteEveryGroup(currentUser, everyGroupEntity);
        everyGroupRepository.delete(everyGroupEntity);
    }

    @Override
    @Transactional
    public void validateApplyEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity) {
        validateAlreadyApplied(currentUser, everyGroupEntity);
        validateHostAccess(currentUser, everyGroupEntity);
        validateGroupFull(everyGroupEntity);
    }

    @Override
    public boolean validateCancelEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity) {
        if(everyGroupEntity.isHost(currentUser))
            throw new GongBaekException(ResponseError.UNAUTHORIZED_ACCESS);
        return everyGroupEntity.isApply(currentUser);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateStatus() {
        List<EveryGroupEntity> everyGroupEntities = everyGroupRepository.findAllByNotStatus(
            Status.CLOSED);
        everyGroupEntities.forEach(groupStatusUpdater::updateEveryGroup);
    }

    @Override
    public boolean isSameSchoolEveryGroup(UserEntity currentUser, EveryGroupVo groupVo) {
        String userSchool = currentUser.getSchool().getSchoolName();
        EveryGroupEntity everyGroupEntity = findEveryGroupEntityByGroupId(
            groupVo.groupId());
        String groupCreatorSchool = everyGroupEntity.getUserEntity().getSchool().getSchoolName();

        return userSchool.equals(groupCreatorSchool);
    }

    private void validateDeleteEveryGroup(UserEntity currentUser, EveryGroupEntity everyGroupEntity) {
        if (!everyGroupEntity.isHost(currentUser))
            throw new GongBaekException(ResponseError.UNAUTHORIZED_ACCESS);
    }

    private EveryGroupEntity buildEveryGroupEntity(RegisterGroupServiceRequest serviceRequest,
        GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {

        LocalDate nowDate = LocalDate.now();
        int month = nowDate.getMonth().getValue();

        return EveryGroupEntity.builder()
            .category(serviceRequest.category())
            .dueDate(dueDateExtractor(month, nowDate))
            .coverImg(serviceRequest.coverImg())
            .location(serviceRequest.location())
            .status(Status.RECRUITING)
            .maxPeopleCount(serviceRequest.maxPeopleCount())
            .gongbaekTimeSlotEntity(gongbaekTimeSlotEntity)
            .title(serviceRequest.groupTitle())
            .introduction(serviceRequest.introduction())
            .userEntity(serviceRequest.userEntity())
            .build();
    }

    private LocalDate dueDateExtractor(int month, LocalDate nowDate) {
        if(month < 7)
            return  LocalDate.of(nowDate.getYear(), Month.JUNE, 30);
        return LocalDate.of(nowDate.getYear(), Month.DECEMBER, 31);
    }

    private void validateAlreadyApplied(UserEntity currentUser, EveryGroupEntity everyGroupEntity) {
        if (everyGroupEntity.isApply(currentUser)) {
            throw new GongBaekException(ResponseError.APPLY_ALREADY_EXIST);
        }
    }

    private void validateHostAccess(UserEntity currentUser, EveryGroupEntity everyGroupEntity) {
        if (everyGroupEntity.isHost(currentUser)) {
            throw new GongBaekException(ResponseError.UNAUTHORIZED_ACCESS);
        }
    }

    private void validateGroupFull(EveryGroupEntity everyGroupEntity) {
        if (everyGroupEntity.getCurrentPeopleCount() == everyGroupEntity.getMaxPeopleCount()) {
            throw new GongBaekException(ResponseError.GROUP_ALREADY_FULL);
        }
    }

    private EveryGroupEntity findIdOrThrow(final long groupId) {
        return everyGroupRepository.findById(groupId).orElseThrow(
            () -> new GongBaekException(ResponseError.GROUP_NOT_FOUND)
        );
    }

}
