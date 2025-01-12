package com.ggang.be.domain.lectureTimeSlot.application;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.lectureTimeSlot.service.LectureTimeSlotService;
import com.ggang.be.domain.lectureTimeSlot.dto.LectureTimeSlotRequest;
import com.ggang.be.domain.lectureTimeSlot.vo.LectureTimeSlotVo;
import com.ggang.be.domain.lectureTimeSlot.infra.LectureTimeSlotRepository;
import com.ggang.be.domain.user.UserEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureTimeSlotServiceImpl implements LectureTimeSlotService {

    private final LectureTimeSlotRepository lectureTimeSlotRepository;


    @Override
    @Transactional
    public void saveLectureTimeSlot(List<LectureTimeSlotVo> timeTableVos, UserEntity userEntity) {
        timeTableVos.stream()
                .map(lectureTimeSlotVo -> LectureTimeSlotVo.toEntity(lectureTimeSlotVo, userEntity))
                .forEach(lectureTimeSlotRepository::save);
    }

    @Override
    public void isExistInLectureTImeSlot(UserEntity findUserEntity, LectureTimeSlotRequest dto) {
        if(lectureTimeSlotRepository.isInTime(
                dto.startTime(), dto.endTime(), findUserEntity, dto.weekDate()
        ))
            throw new GongBaekException(ResponseError.TIME_SLOT_ALREADY_EXIST);
    }

}
