package com.ggang.be.domain.gongbaekTimeSlot;

import com.ggang.be.domain.BaseTimeEntity;
import com.ggang.be.domain.constant.WeekDate;
import com.ggang.be.domain.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity(name = "gongbaek_time_slot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GongbaekTimeSlotEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WeekDate weekDate;

    private double startTime;
    private double endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;


    @Builder
    private GongbaekTimeSlotEntity(WeekDate weekDate, double startTime, double endTime,
        UserEntity userEntity) {
        this.weekDate = weekDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userEntity = userEntity;
    }
}
