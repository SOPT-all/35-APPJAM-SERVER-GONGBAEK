package com.ggang.be.domain.lectureTimeSlot.infra;

import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.lectureTimeSlot.LectureTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LectureTimeSlotRepository extends JpaRepository<LectureTimeSlotEntity, Long> {


    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END " +
        "FROM lecture_time_slot l " +
        "WHERE l.userEntity = :userEntity " +
        "AND l.weekDate = :weekDate " +
        "AND (" +
        "     (l.startTime <= :startTime AND l.endTime > :startTime) " +
        "  OR (l.startTime < :endTime AND l.endTime >= :endTime) " +
        "  OR (l.startTime > :startTime AND l.endTime < :endTime)" +
        ")")
    boolean isInTime(double startTime, double endTime, UserEntity userEntity, WeekDate weekDate);
}
