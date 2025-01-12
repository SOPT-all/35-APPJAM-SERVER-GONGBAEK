package com.ggang.be.api.facade;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.gongbaekTimeSlot.service.GongbaekTimeSlotService;
import com.ggang.be.api.group.dto.GroupResponseDto;
import com.ggang.be.api.group.dto.RegisterGongbaekRequest;
import com.ggang.be.api.group.dto.RegisterGongbaekResponse;
import com.ggang.be.api.group.everyGroup.service.EveryGroupService;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.api.mapper.GroupResponseMapper;
import com.ggang.be.api.user.service.UserService;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.gongbaekTimeSlot.dto.GongbaekTimeSlotRequest;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.user.dto.UserInfo;
import com.ggang.be.global.annotation.Facade;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class GroupFacade {

    private final EveryGroupService everyGroupService;
    private final OnceGroupService onceGroupService;
    private final UserService userService;
    private final GongbaekTimeSlotService gongbaekTimeSlotService;

    public GroupResponseDto getGroupInfo(GroupType groupType, Long groupId, String accessToken) {
        UserEntity currentUser = userService.getUserById(Long.parseLong(accessToken));

        return switch (groupType) {
            case WEEKLY -> GroupResponseMapper.fromEveryGroup(
                    everyGroupService.getEveryGroupDetail(groupId, currentUser)
            );
            case ONCE -> GroupResponseMapper.fromOnceGroup(
                    onceGroupService.getOnceGroupDetail(groupId, currentUser)
            );
        };
    }

    public UserInfo getGroupUserInfo(GroupType groupType, Long groupId, String accessToken) {
        userService.getUserById(Long.parseLong(accessToken));

        long userId = switch (groupType) {
            case WEEKLY -> everyGroupService.getEveryGroupRegisterUserId(groupId);
            case ONCE -> onceGroupService.getOnceGroupRegisterUserId(groupId);
        };

        return UserInfo.of(userService.getUserById(userId));
    }

    @Transactional
    public RegisterGongbaekResponse registerGongbaek(Long userId, RegisterGongbaekRequest dto) {
        UserEntity findUserEntity = userService.getUserById(userId);

        GongbaekTimeSlotRequest gongbaekDto = convertDtoToGongbaekDto(dto, findUserEntity);
        RegisterGroupServiceRequest serviceRequest = convertDtoToServiceDto(dto, findUserEntity);

        GongbaekTimeSlotEntity gongbaekTimeSlotEntity = gongbaekTimeSlotService.registerGongbaekTimeSlot(
            findUserEntity, gongbaekDto);


        if(dto.groupType() == GroupType.WEEKLY) 
            return RegisterGongbaekResponse.of(everyGroupService.registerEveryGroup(serviceRequest, gongbaekTimeSlotEntity));
        if(dto.groupType() == GroupType.ONCE) 
            return RegisterGongbaekResponse.of(onceGroupService.registerOnceGroup(serviceRequest, gongbaekTimeSlotEntity));

        throw new GongBaekException(ResponseError.BAD_REQUEST);
    }

    private RegisterGroupServiceRequest convertDtoToServiceDto(RegisterGongbaekRequest dto,
        UserEntity findUserEntity) {
        return RegisterGongbaekRequest.toServiceRequest(
            findUserEntity, dto);
    }

    private GongbaekTimeSlotRequest convertDtoToGongbaekDto(RegisterGongbaekRequest dto,
        UserEntity findUserEntity) {
        return RegisterGongbaekRequest.toGongbaekTimeSlotRequest(
            findUserEntity, dto);
    }


}
