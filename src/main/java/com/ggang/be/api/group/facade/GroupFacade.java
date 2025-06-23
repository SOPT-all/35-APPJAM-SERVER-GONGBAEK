package com.ggang.be.api.group.facade;

import com.ggang.be.api.group.ActiveCombinedGroupVoPreparer;
import com.ggang.be.api.group.CombinedNearestGroupVo;
import com.ggang.be.api.group.CombinedNearestGroupVoPreparer;
import com.ggang.be.api.group.GroupVoAggregator;
import com.ggang.be.api.group.dto.*;
import com.ggang.be.api.group.registry.*;
import com.ggang.be.api.lectureTimeSlot.service.LectureTimeSlotService;
import com.ggang.be.api.user.service.UserService;
import com.ggang.be.domain.block.application.BlockServiceImpl;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.constant.FillGroupType;
import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.group.dto.GroupVo;
import com.ggang.be.domain.group.vo.NearestGroup;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.user.dto.UserInfo;
import com.ggang.be.global.annotation.Facade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Facade
@RequiredArgsConstructor
@Slf4j
public class GroupFacade {

    private final UserService userService;
    private final LectureTimeSlotService lectureTimeSlotService;
    private final LatestGroupStrategyRegistry latestGroupStrategyRegistry;
    private final ReadFillMemberStrategyRegistry readFillMemberStrategyRegistry;
    private final GroupInfoStrategyRegistry groupInfoStrategyRegistry;
    private final ApplyGroupStrategyRegistry applyGroupStrategyRegistry;
    private final RegisterGroupStrategyRegistry registerGroupStrategyRegistry;
    private final PrepareRegisterGongbaekFacade prepareRegisterGongbaekFacade;
    private final CancelGroupStrategyRegistry cancelGroupStrategyRegistry;
    private final DeleteGroupStrategyRegistry deleteGroupStrategyRegistry;
    private final GroupUserInfoStrategyRegistry groupUserInfoStrategyRegistry;
    private final ActiveCombinedGroupVoPreparer activeCombinedGroupVoPreparer;
    private final MyGroupStrategyRegistry myGroupStrategyRegistry;
    private final NearestGroupResponseStrategyRegistry nearestGroupResponseStrategyRegistry;
    private final CombinedNearestGroupVoPreparer combinedNearestGroupVoPreparer;
    private final FindGroupCreatorStrategyRegistry findGroupCreatorStrategyRegistry;
    private final BlockServiceImpl blockService;


    public GroupCreatorVo findGroupCreator(GroupType groupType, Long groupId){
        FindGroupCreatorStrategy groupCreatorStrategy = findGroupCreatorStrategyRegistry.findGroupCreatorStrategy(
            groupType);

        return groupCreatorStrategy.findCreator(groupId);
    }


    public GroupResponse getGroupInfo(GroupType groupType, Long groupId, long userId) {
        UserEntity currentUser = userService.getUserById(userId);
        GroupInfoStrategy groupInfoStrategy = groupInfoStrategyRegistry.getGroupInfo(groupType);

        return groupInfoStrategy.getGroupInfo(groupId, currentUser);
    }

    public NearestGroupResponse getNearestGroupInfo(long userId) {
        UserEntity currentUser = userService.getUserById(userId);

        CombinedNearestGroupVo prepare = combinedNearestGroupVoPreparer.prepare(currentUser);

        NearestGroup nearestEveryGroup = prepare.nearestEveryGroup();
        NearestGroup nearestOnceGroup = prepare.nearestOnceGroup();

        NearestGroupResponseStrategy strategy = nearestGroupResponseStrategyRegistry.getStrategy(
            nearestEveryGroup, nearestOnceGroup);

        return strategy.getNearestGroupResponse(nearestEveryGroup, nearestOnceGroup);

    }

    public UserInfo getGroupUserInfo(GroupType groupType, Long groupId) {
        GroupUserInfoStrategy groupUserInfoStrategy
                = groupUserInfoStrategyRegistry.getGroupUserInfo(groupType);

        return UserInfo.of(
            userService.getUserById(groupUserInfoStrategy.getGroupUserInfo(groupId)));
    }

    @Transactional
    public RegisterGroupResponse registerGroup(Long userId, RegisterGongbaekRequest dto) {

        PrepareRegisterInfo prepareRegisterInfo = prepareRegisterGongbaekFacade.prepareInfo(userId, dto);

        RegisterGroupStrategy registerGroupStrategy
                = registerGroupStrategyRegistry.getRegisterGroupStrategy(dto.groupType());

        return registerGroupStrategy.registerGroup(prepareRegisterInfo);
    }

    @Transactional
    public void applyGroup(Long userId, GroupRequest requestDto) {
        UserEntity findUserEntity = userService.getUserById(userId);

        ApplyGroupStrategy applyGroupStrategy = applyGroupStrategyRegistry.getApplyGroupStrategy(
            requestDto.groupType()
        );

        applyGroupStrategy.applyGroup(findUserEntity, requestDto);
    }

    @Transactional
    public void cancelMyApplication(Long userId, GroupRequest requestDto) {
        UserEntity findUserEntity = userService.getUserById(userId);

        CancelGroupStrategy cancelGroupStrategy = cancelGroupStrategyRegistry.getCancelGroupStrategy(
            requestDto.groupType()
        );

        cancelGroupStrategy.cancelGroup(findUserEntity, requestDto);
    }

    @Transactional
    public void deleteMyGroup(Long userId, GroupRequest requestDto) {
        UserEntity findUserEntity = userService.getUserById(userId);

        DeleteGroupStrategy deleteGroupStrategy = deleteGroupStrategyRegistry.getDeleteGroupStrategy(
                requestDto.groupType()
        );

        deleteGroupStrategy.deleteGroup(findUserEntity, requestDto);
    }

    public List<MyGroupResponse> getMyGroups(long userId, FillGroupType category, boolean status) {
        UserEntity currentUser = userService.getUserById(userId);

        MyGroupStrategy groupStrategy = myGroupStrategyRegistry.getGroupStrategy(category);

        List<GroupVo> groupResponses = groupStrategy.getGroups(currentUser, status);

        return groupResponses.stream()
                .map(MyGroupResponse::fromGroupVo)
                .collect(Collectors.toList());
    }

    public List<ActiveGroupsResponse> getFillGroups(long userId, Category category) {
        UserEntity currentUser = userService.getUserById(userId);

        List<String> userBlocks = blockService.findUserBlocks(userId);

        CombinedGroupVos preparedGroupVo = activeCombinedGroupVoPreparer.prepareGroupVos(
            currentUser, category, userBlocks);

        List<GroupVo> groupVos = GroupVoAggregator.aggregateAndSort(
            preparedGroupVo.everyGroupVos(),
            preparedGroupVo.onceGroupVos()
        );

        return groupVos.stream()
            .filter(groupVo -> lectureTimeSlotService.isActiveGroupsInLectureTimeSlot(currentUser,
                groupVo))
            .map(ActiveGroupsResponse::fromGroupVo)
            .collect(Collectors.toList());
    }

    public List<LatestResponse> getLatestGroups(long userId, GroupType groupType) {
        UserEntity currentUser = userService.getUserById(userId);

        LatestGroupStrategy latestGroupStrategy = latestGroupStrategyRegistry.getGroupStrategy(
            groupType);

        return latestGroupStrategy.getLatestGroups(currentUser).stream()
            .filter(groupVo -> lectureTimeSlotService.isActiveGroupsInLectureTimeSlot(currentUser, groupVo))
            .sorted((group1, group2) -> group2.createdAt().compareTo(group1.createdAt()))
            .limit(5).map(LatestResponse::fromGroupVo)
            .collect(Collectors.toList());
    }


    public ReadFillMembersResponse getGroupUsersInfo(Long userId, ReadFillMembersRequest dto) {
        UserEntity findUserEntity = userService.getUserById(userId);

        log.info("find strategy");

        ReadFillMemberStrategy strategy = readFillMemberStrategyRegistry.getStrategy(
            dto.groupType());

        log.info("finish strategy");

        return strategy.getGroupUsersInfo(findUserEntity, dto);
    }

}
