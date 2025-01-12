package com.ggang.be.api.group.controller;

import com.ggang.be.api.common.ApiResponse;
import com.ggang.be.api.common.ResponseBuilder;
import com.ggang.be.api.common.ResponseSuccess;
import com.ggang.be.api.facade.GongbaekRequestFacade;
import com.ggang.be.api.facade.GroupFacade;
import com.ggang.be.api.facade.GroupType;
import com.ggang.be.api.group.dto.GroupRequestDto;
import com.ggang.be.api.group.dto.GroupResponseDto;
import com.ggang.be.api.group.dto.GroupUserInfoResponseDto;
import com.ggang.be.api.group.dto.RegisterGongbaekRequest;
import com.ggang.be.api.group.dto.RegisterGongbaekResponse;
import com.ggang.be.global.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GroupController {
    private final GroupFacade groupFacade;
    private final GongbaekRequestFacade gongbaekRequestFacade;
    private final JwtService jwtService;

    @GetMapping("/fill/info")
    public ResponseEntity<ApiResponse<GroupResponseDto>> getGroupInfo(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody final GroupRequestDto groupRequestDto
    ) {
        GroupResponseDto groupResponseDto = groupFacade.getGroupInfo(
                GroupType.fromString(groupRequestDto.groupType()), groupRequestDto.groupId(), accessToken
        );
        return ResponseEntity.ok(ApiResponse.success(ResponseSuccess.OK, groupResponseDto));
    }

    @GetMapping("/fill/user/info")
    public ResponseEntity<ApiResponse<GroupUserInfoResponseDto>> getGroupUserInfo(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody final GroupRequestDto groupRequestDto
    ){
        GroupUserInfoResponseDto groupUserInfoResponseDto = GroupUserInfoResponseDto.of(groupFacade.getGroupUserInfo(
                GroupType.fromString(groupRequestDto.groupType()), groupRequestDto.groupId(), accessToken
        ));
        return ResponseEntity.ok(ApiResponse.success(ResponseSuccess.OK, groupUserInfoResponseDto));
    }

    // TODO : 테스트 코드 작성 진행해야함!
    @PostMapping("/gongbaek")
    public ResponseEntity<ApiResponse<RegisterGongbaekResponse>> registerGongbaek(@RequestHeader("Authorization") String token,
        @RequestBody final RegisterGongbaekRequest dto){
        Long userId = jwtService.parseTokenAndGetUserId(token);

        gongbaekRequestFacade.validateRegisterRequest(userId, dto);

        return ResponseBuilder.ok(groupFacade.registerGongbaek(userId, dto));
    }


}
