package com.ggang.be.domain.gongbaekTimeSlot.application;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.gongbaekTimeSlot.service.GongbaekTimeSlotService;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.gongbaekTimeSlot.dto.GongbaekTimeSlotRequest;
import com.ggang.be.domain.gongbaekTimeSlot.infra.GongbaekTimeSlotRepository;
import com.ggang.be.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GongbaekTimeSlotServiceImpl implements GongbaekTimeSlotService {

    private final GongbaekTimeSlotRepository gongbaekTimeSlotRepository;

    @Override
    @Transactional
    public GongbaekTimeSlotEntity registerGongbaekTimeSlot(UserEntity userEntity, GongbaekTimeSlotRequest dto) {

        GongbaekTimeSlotEntity buildEntity = GongbaekTimeSlotEntity.builder()
            .userEntity(userEntity)
            .startTime(dto.startTime())
            .endTime(dto.endTime())
            .weekDate(dto.weekDay())
            .build();

        return gongbaekTimeSlotRepository.save(buildEntity);
    }

    @Override
    public void isExistInWeekDateGongbaekTimeSlot(UserEntity findUserEntity,
        GongbaekTimeSlotRequest dto) {
        if(gongbaekTimeSlotRepository.existsByStartTimeAndEndTimeAndUserEntityAndWeekDate(
            dto.startTime(), dto.endTime(), findUserEntity, dto.weekDay()
        ))
            throw new GongBaekException(ResponseError.GONGBAEK_TIME_SLOT_ALREADY_EXIST);
    }



}
