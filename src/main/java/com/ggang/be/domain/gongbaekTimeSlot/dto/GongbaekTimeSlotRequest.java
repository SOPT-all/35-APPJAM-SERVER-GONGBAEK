package com.ggang.be.domain.gongbaekTimeSlot.dto;

import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.user.UserEntity;

public record GongbaekTimeSlotRequest(UserEntity userEntity, double startTime, double endTime, WeekDate weekDay) {



}
