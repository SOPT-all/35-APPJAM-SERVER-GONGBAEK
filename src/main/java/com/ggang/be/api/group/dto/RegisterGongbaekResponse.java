package com.ggang.be.api.group.dto;

public record RegisterGongbaekResponse(Long groupId) {

    public static RegisterGongbaekResponse of(Long groupId) {
        return new RegisterGongbaekResponse(groupId);
    }

}
