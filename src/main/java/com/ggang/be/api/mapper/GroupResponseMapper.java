package com.ggang.be.api.mapper;

import com.ggang.be.api.facade.GroupType;
import com.ggang.be.api.group.dto.GroupResponseDto;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupDto;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupDto;

public record GroupResponseMapper() {

    public static GroupResponseDto fromOnceGroup(OnceGroupDto dto) {
        return new GroupResponseDto(
            dto.groupId(),
            GroupType.ONCE.toString(),
            dto.groupTitle(),
            dto.location(),
            dto.status(),
            dto.isHost(),
            dto.isApply(),
            dto.currentPeopleCount(),
            dto.maxPeopleCount(),
            dto.introduction(),
            dto.category(),
            dto.coverImg(),
            dto.weekDay(),
            dto.weekDate() != null ? dto.weekDate() : null,
            dto.gongbaekTimeSlotEntity().getStartTime(),
            dto.gongbaekTimeSlotEntity().getEndTime()
        );
    }

    public static GroupResponseDto fromEveryGroup(EveryGroupDto dto) {
        return new GroupResponseDto(
            dto.groupId(),
            GroupType.WEEKLY.toString(),
            dto.title(),
            dto.location(),
            dto.status(),
            dto.isHost(),
            dto.isApply(),
            dto.currentPeopleCount(),
            dto.maxPeopleCount(),
            dto.introduction(),
            dto.category(),
            dto.coverImg(),
            dto.gongbaekTimeSlotEntity().getWeekDate().name(),
            null,
            dto.gongbaekTimeSlotEntity().getStartTime(),
            dto.gongbaekTimeSlotEntity().getEndTime()
        );
    }
}
