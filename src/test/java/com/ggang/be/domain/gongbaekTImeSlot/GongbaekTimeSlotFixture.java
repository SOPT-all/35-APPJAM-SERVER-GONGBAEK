package com.ggang.be.domain.gongbaekTImeSlot;

import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;

public class GongbaekTimeSlotFixture {

    public static GongbaekTimeSlotEntity getTestGongbaekTimeSlot() {
        return GongbaekTimeSlotEntity.builder()
            .startTime(10)
            .endTime(12)
            .weekDate(WeekDate.MON)
            .build();
    }

}
