package com.ggang.be.api.comment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.group.vo.ReadCommentGroup;

public record ReadCommentResponse(@JsonUnwrapped ReadCommentGroup readCommentGroup) {
    public static ReadCommentResponse of(ReadCommentGroup readCommentGroup) {
        return new ReadCommentResponse(readCommentGroup);
    }

    public ReadCommentResponse withFilteredComments(List<GroupCommentVo> list) {
        return new ReadCommentResponse(this.readCommentGroup.withComments(list));
    }
}
