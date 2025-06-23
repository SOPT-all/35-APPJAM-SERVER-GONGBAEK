package com.ggang.be.domain.group.vo;

import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.group.everyGroup.vo.ReadEveryGroupCommentCommonVo;
import com.ggang.be.domain.group.onceGroup.vo.ReadOnceGroupCommentCommonVo;
import java.util.List;

public record ReadCommentGroup(int commentCount, long groupId, GroupType groupType,
                               Status groupStatus, List<GroupCommentVo> comments) {
    public static ReadCommentGroup fromEveryGroup(ReadEveryGroupCommentCommonVo vo,
        List<GroupCommentVo> comments) {
        return new ReadCommentGroup(vo.commentCount(),
            vo.groupId(),
            vo.groupType(),
            vo.status(),
            comments);
    }


    public static ReadCommentGroup fromOnceGroup(ReadOnceGroupCommentCommonVo vo,
        List<GroupCommentVo> comments) {
        return new ReadCommentGroup(vo.commentCount(),
            vo.groupId(),
            vo.groupType(),
            vo.status(),
            comments);
    }

    public ReadCommentGroup withComments(List<GroupCommentVo> commentVos) {
        return new ReadCommentGroup(commentCount,
            groupId,
            groupType,
            groupStatus,
            commentVos);
    }
}
