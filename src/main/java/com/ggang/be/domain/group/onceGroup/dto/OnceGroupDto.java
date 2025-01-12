package com.ggang.be.domain.group.onceGroup.dto;

import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.user.UserEntity;

public record OnceGroupDto(
    long groupId,
    String groupTitle,
    String location,
    String status,
    int currentPeopleCount,
    int maxPeopleCount,
    boolean isHost,
    boolean isApply,
    String introduction,
    String category,
    int coverImg,
    String weekDay,
    String weekDate,
    GongbaekTimeSlotEntity gongbaekTimeSlotEntity
) {

    public static OnceGroupDto toDto(OnceGroupEntity entity, UserEntity currentUser) {
        return new OnceGroupDto(
            entity.getId(),
            entity.getTitle(),
            entity.getLocation(),
            entity.getStatus().name(),
            entity.getCurrentPeopleCount(),
            entity.getMaxPeopleCount(),
            entity.isHost(currentUser),
            entity.isApply(currentUser),
            entity.getIntroduction(),
            entity.getCategory().toString(),
            entity.getCoverImg(),
            WeekDate.fromDayOfWeek(entity.getGroupDate().getDayOfWeek()).name(),
            entity.getGroupDate().toString(),
            GongbaekTimeSlotEntity.builder()
                .startTime(entity.getGongbaekTimeSlotEntity().getStartTime())
                .endTime(entity.getGongbaekTimeSlotEntity().getEndTime())
                .weekDate(WeekDate.fromDayOfWeek(entity.getGroupDate().getDayOfWeek()))
                .userEntity(currentUser)
                .build()
        );
    }

}
