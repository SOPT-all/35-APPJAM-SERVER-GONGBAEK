package com.ggang.be.api.gongbaekTimeSlot.service;

import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.gongbaekTimeSlot.dto.GongbaekTimeSlotRequest;
import com.ggang.be.domain.user.UserEntity;

public interface GongbaekTimeSlotService {
    GongbaekTimeSlotEntity registerGongbaekTimeSlot(UserEntity userEntity, GongbaekTimeSlotRequest dto);

    void isExistInWeekDateGongbaekTimeSlot(UserEntity findUserEntity, GongbaekTimeSlotRequest gongbaekDto);
}
