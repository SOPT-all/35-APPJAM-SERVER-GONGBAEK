package com.ggang.be.api.lectureTimeSlot.service;

import com.ggang.be.domain.lectureTimeSlot.dto.LectureTimeSlotRequest;
import com.ggang.be.domain.lectureTimeSlot.vo.LectureTimeSlotVo;
import com.ggang.be.domain.user.UserEntity;
import java.util.List;

public interface LectureTimeSlotService {
    void saveLectureTimeSlot(List<LectureTimeSlotVo> timeTableVos, UserEntity userEntity);

    void isExistInLectureTImeSlot(UserEntity findUserEntity, LectureTimeSlotRequest dto);
}
