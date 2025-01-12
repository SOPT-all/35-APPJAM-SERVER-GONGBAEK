package com.ggang.be.domain.group.dto;

import com.ggang.be.api.facade.GroupType;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;
import java.time.LocalDate;

public record RegisterGroupServiceRequest(
    UserEntity userEntity,
    GroupType groupType,
    LocalDate weekDate,
    WeekDate weekDay,
    double startTime,
    double endTime,
    LocalDate dueDate,
    Category category,
    int coverImg,
    String location,
    int maxPeopleCount,
    String groupTitle,
    String introduction
) {


}
