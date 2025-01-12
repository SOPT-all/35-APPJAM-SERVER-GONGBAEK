package com.ggang.be.api.facade;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.gongbaekTimeSlot.service.GongbaekTimeSlotService;
import com.ggang.be.api.group.dto.RegisterGongbaekRequest;
import com.ggang.be.api.group.everyGroup.service.EveryGroupService;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.api.lectureTimeSlot.service.LectureTimeSlotService;
import com.ggang.be.api.user.service.UserService;
import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.gongbaekTimeSlot.dto.GongbaekTimeSlotRequest;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.lectureTimeSlot.dto.LectureTimeSlotRequest;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.global.annotation.Facade;
import com.ggang.be.global.util.TimeValidator;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Facade
@RequiredArgsConstructor
@Slf4j
public class GongbaekRequestFacade {

    private final LectureTimeSlotService  lectureTimeSlotService;
    private final GongbaekTimeSlotService gongbaekTimeSlotService;
    private final OnceGroupService onceGroupService;
    private final EveryGroupService everyGroupService;
    private final UserService userService;


    public void validateRegisterRequest(Long userId, RegisterGongbaekRequest dto) {
        TimeValidator.isTimeValid(dto.startTime(), dto.endTime());
        isDateValid(dto);
        isWeekDateRight(dto);

        UserEntity findUserEntity = userService.getUserById(userId);

        isRequestTimeTableValid(dto, findUserEntity);

        checkLectureTimeSlot(dto, findUserEntity); // 지금 해당 요일에 강의시간표가 들어가져있는지? 확인
        checkGongbaekTimeSlot(dto, findUserEntity);
    }


    private void isRequestTimeTableValid(RegisterGongbaekRequest dto, UserEntity findUserEntity) {
        RegisterGroupServiceRequest serviceRequest = RegisterGongbaekRequest.toServiceRequest(
            findUserEntity, dto);
        everyGroupService.isExistedInTime(serviceRequest); //지금 요일에 주차별 모임에 해당 시간표가 들어가져있는지?  확인
        onceGroupService.isExistInOnceGroupTimeSlot(serviceRequest); // 지금 해당 일자에 모임이 있는지
    }

    private void isDateValid(RegisterGongbaekRequest dto) {
        TimeValidator.isDateBeforeNow(dto.weekDate());
        TimeValidator.isDateBeforeNow(dto.dueDate());
    }

    private void isWeekDateRight(RegisterGongbaekRequest dto) {
        if(dto.groupType() == GroupType.ONCE)
            TimeValidator.isWeekDateRight(dto.weekDay(), dto.weekDate());
    }

    private void checkGongbaekTimeSlot(RegisterGongbaekRequest dto, UserEntity findUserEntity) {
        GongbaekTimeSlotRequest gongbaekDto = RegisterGongbaekRequest.toGongbaekTimeSlotRequest(
            findUserEntity, dto);
        gongbaekTimeSlotService.isExistInWeekDateGongbaekTimeSlot(findUserEntity, gongbaekDto);
    }

    private void checkLectureTimeSlot(RegisterGongbaekRequest dto, UserEntity findUserEntity) {
        LectureTimeSlotRequest lectureTimeSlotRequest = RegisterGongbaekRequest.toLectureTimeSlotRequest(
            findUserEntity, dto);
        lectureTimeSlotService.isExistInLectureTImeSlot(findUserEntity, lectureTimeSlotRequest);
    }



}
