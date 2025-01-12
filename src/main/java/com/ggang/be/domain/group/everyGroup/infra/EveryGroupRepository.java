package com.ggang.be.domain.group.everyGroup.infra;

import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.user.UserEntity;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EveryGroupRepository extends JpaRepository<EveryGroupEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN TRUE ELSE FALSE END " +
        "FROM every_group o " +
        "WHERE o.userEntity = :userEntity " +
        "AND o.gongbaekTimeSlotEntity.weekDate = :weekDate " +
        "AND o.status != :status " +
        "AND (" +
        "     (o.gongbaekTimeSlotEntity.startTime <= :startTime AND o.gongbaekTimeSlotEntity.endTime > :startTime) " +
        "  OR (o.gongbaekTimeSlotEntity.startTime < :endTime AND o.gongbaekTimeSlotEntity.endTime >= :endTime) " +
        "  OR (o.gongbaekTimeSlotEntity.startTime > :startTime AND o.gongbaekTimeSlotEntity.endTime < :endTime)" +
        ")")
    boolean isInTime(UserEntity userEntity, double startTime, double endTime,
       WeekDate weekDate, Status status);

}