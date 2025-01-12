package com.ggang.be.domain.lectureTimeSlot.dto;

import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.user.UserEntity;

public record LectureTimeSlotRequest(UserEntity userEntity, double startTime, double endTime, WeekDate weekDate) {

    public static LectureTimeSlotRequest of(UserEntity userEntity, double startTime, double endTime, WeekDate weekDate) {
        return new LectureTimeSlotRequest(userEntity, startTime, endTime, weekDate);
    }
}
