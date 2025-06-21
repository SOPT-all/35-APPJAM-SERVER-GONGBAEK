package com.ggang.be.api.group.dto;

public record GroupCreatorVo(long creatorId) {
	public static GroupCreatorVo from(long creatorId){
		return new GroupCreatorVo(creatorId);
	}
}
