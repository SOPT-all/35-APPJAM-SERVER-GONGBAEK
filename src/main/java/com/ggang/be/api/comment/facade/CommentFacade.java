package com.ggang.be.api.comment.facade;

import java.util.List;

import com.ggang.be.api.comment.dto.*;
import com.ggang.be.api.comment.registry.CommentStrategy;
import com.ggang.be.api.comment.registry.CommentStrategyRegistry;
import com.ggang.be.api.comment.service.CommentService;
import com.ggang.be.api.user.service.UserService;
import com.ggang.be.domain.block.application.BlockServiceImpl;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.global.annotation.Facade;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentStrategyRegistry commentStrategyRegistry;
    private final UserService userService;
    private final CommentService commentService;
    private final BlockServiceImpl blockService;

    @Transactional
    public WriteCommentResponse writeComment(final long userId, WriteCommentRequest dto) {

        CommentStrategy commentStrategy = commentStrategyRegistry.getCommentGroupStrategy(dto.groupType());

        UserEntity findUserEntity = userService.getUserById(userId);
        CommentEntity commentEntity = commentService.writeComment(findUserEntity, dto);

        return commentStrategy.writeComment(userId, dto, WriteCommentEntityDto.from(commentEntity, findUserEntity));

    }

    public ReadCommentResponse readComment(Long userId, final boolean isPublic, ReadCommentRequest dto) {

        CommentStrategy commentStrategy = commentStrategyRegistry.getCommentGroupStrategy(dto.groupType());

        UserEntity findUserEntity = userService.getUserById(userId);

        // 여기에서 다 처리해줘야함! 사실! 그러니까 comments 에 대해서 만드는 것은 밖에서 진행해줘야함!

        List<String> userBlocks = blockService.findUserBlocks(findUserEntity.getId());

        ReadCommentResponse readCommentResponse = commentStrategy.readComment(findUserEntity, isPublic, dto);

        List<GroupCommentVo> filterCommentVos = readCommentResponse.readCommentGroup()
            .comments()
            .stream()
            .filter(c -> !userBlocks.contains(c.nickname()))
            .toList();

        return readCommentResponse.withFilteredComments(filterCommentVos);
    }

}
