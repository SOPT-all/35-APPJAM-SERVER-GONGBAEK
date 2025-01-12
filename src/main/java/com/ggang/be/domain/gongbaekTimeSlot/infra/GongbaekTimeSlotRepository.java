package com.ggang.be.domain.gongbaekTimeSlot.infra;

import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GongbaekTimeSlotRepository extends JpaRepository<GongbaekTimeSlotEntity, Long> {

    boolean existsByStartTimeAndEndTimeAndUserEntityAndWeekDate(double startTime, double endTime, UserEntity userEntity, WeekDate weekDate);
}
