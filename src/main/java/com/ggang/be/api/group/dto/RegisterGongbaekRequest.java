package com.ggang.be.api.group.dto;

import com.ggang.be.api.facade.GroupType;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.gongbaekTimeSlot.dto.GongbaekTimeSlotRequest;
import com.ggang.be.domain.group.dto.RegisterGroupServiceRequest;
import com.ggang.be.domain.lectureTimeSlot.dto.LectureTimeSlotRequest;
import com.ggang.be.domain.user.UserEntity;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

public record RegisterGongbaekRequest(
    GroupType groupType,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate weekDate,
    WeekDate weekDay,
    double startTime,
    double endTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDate,
    Category category,
    int coverImg,
    String location,
    int maxPeopleCount,
    String groupTitle,
    String introduction
) {


    public static LectureTimeSlotRequest toLectureTimeSlotRequest(UserEntity userEntity,
        RegisterGongbaekRequest request) {
        if (Objects.isNull(request.weekDay))
            return LectureTimeSlotRequest.of(
                userEntity,
                request.startTime(),
                request.endTime(),
                WeekDate.fromDayOfWeek(request.weekDate().getDayOfWeek())
            );
        else
            return LectureTimeSlotRequest.of(
                userEntity,
                request.startTime(),
                request.endTime(),
                request.weekDay()
            );
    }

    public static GongbaekTimeSlotRequest toGongbaekTimeSlotRequest(UserEntity userEntity,
        RegisterGongbaekRequest request) {

        if (Objects.isNull(request.weekDay)) {
            return new GongbaekTimeSlotRequest(
                userEntity,
                request.startTime(),
                request.endTime(),
                WeekDate.fromDayOfWeek(request.weekDate().getDayOfWeek())
            );
        } else {
            return new GongbaekTimeSlotRequest(
                userEntity,
                request.startTime(),
                request.endTime(),
                request.weekDay
            );
        }


    }

    public static RegisterGroupServiceRequest toServiceRequest(UserEntity userEntity,
        RegisterGongbaekRequest request) {
        return new RegisterGroupServiceRequest(
            userEntity,
            request.groupType(),
            request.weekDate(),
            request.weekDay(),
            request.startTime(),
            request.endTime(),
            request.dueDate(),
            request.category(),
            request.coverImg(),
            request.location(),
            request.maxPeopleCount(),
            request.groupTitle(),
            request.introduction()
        );
    }

}
