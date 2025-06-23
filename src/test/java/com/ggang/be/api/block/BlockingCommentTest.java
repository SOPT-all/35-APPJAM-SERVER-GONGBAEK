package com.ggang.be.api.block;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ggang.be.api.comment.dto.ReadCommentRequest;
import com.ggang.be.api.comment.dto.ReadCommentResponse;
import com.ggang.be.api.comment.facade.CommentFacade;
import com.ggang.be.api.comment.registry.CommentStrategy;
import com.ggang.be.api.comment.registry.CommentStrategyRegistry;
import com.ggang.be.api.user.service.UserService;
import com.ggang.be.domain.block.application.BlockServiceImpl;
import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.group.vo.GroupCommentVo;
import com.ggang.be.domain.group.vo.ReadCommentGroup;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.user.fixture.UserEntityFixture;

@ExtendWith(MockitoExtension.class)
class BlockingCommentTest {

    @Mock
    private CommentStrategyRegistry commentStrategyRegistry;

    @Mock
    private UserService userService;

    @Mock
    private BlockServiceImpl blockService;

    @Mock
    private CommentStrategy commentStrategy;

    @InjectMocks
    private CommentFacade commentFacade;

    private UserEntity testUser;
    private ReadCommentRequest readCommentRequest;
    private List<String> blockedUsers;
    private List<GroupCommentVo> commentVos;
    private ReadCommentResponse originalResponse;

    @BeforeEach
    void setUp() {
        // 테스트 기본 데이터 설정
        testUser = UserEntityFixture.createByNickname("testUser");
        ReflectionTestUtils.setField(testUser, "id", 1L); // ID 설정
        readCommentRequest = new ReadCommentRequest(1L, GroupType.ONCE);
        blockedUsers = List.of("blockedUser1", "blockedUser2");

        // 댓글 데이터 생성 (차단된 사용자와 일반 사용자 포함)
        commentVos = createTestCommentData();
        
        // 원본 응답 생성
        ReadCommentGroup commentGroup = new ReadCommentGroup(
            commentVos.size(), 
            1L, 
            GroupType.ONCE, 
            Status.RECRUITING, 
            commentVos
        );
        originalResponse = ReadCommentResponse.of(commentGroup);

        // 공통 Mock 설정
        setupCommonMocks();
    }

    @Test
    @DisplayName("차단된 사용자의 댓글이 필터링되어야 한다")
    void shouldFilterBlockedUserComments() {
        // When
        ReadCommentResponse result = commentFacade.readComment(
            testUser.getId(), true, readCommentRequest);

        // Then
        List<GroupCommentVo> filteredComments = result.readCommentGroup().comments();
        assertThat(filteredComments).hasSize(3);
        assertThat(filteredComments)
            .extracting(GroupCommentVo::nickname)
            .containsOnly("testUser", "normalUser1", "normalUser2")
            .doesNotContain("blockedUser1", "blockedUser2");
    }

    @Test
    @DisplayName("차단 목록이 비어있으면 모든 댓글이 반환되어야 한다")
    void shouldReturnAllCommentsWhenNoBlockedUsers() {
        // Given
        when(blockService.findUserBlocks(testUser.getId()))
            .thenReturn(List.of()); // 빈 차단 목록

        // When
        ReadCommentResponse result = commentFacade.readComment(
            testUser.getId(), true, readCommentRequest);

        // Then
        List<GroupCommentVo> allComments = result.readCommentGroup().comments();
        assertThat(allComments).hasSize(5);
        assertThat(allComments)
            .extracting(GroupCommentVo::nickname)
            .containsExactly("testUser", "normalUser1", "normalUser2", "blockedUser1", "blockedUser2");
    }

    private List<GroupCommentVo> createTestCommentData() {
        LocalDateTime now = LocalDateTime.now();
        
        return List.of(
            createCommentVo(1L, "testUser", now),
            createCommentVo(2L, "normalUser1", now),
            createCommentVo(3L, "normalUser2", now),
            createCommentVo(4L, "blockedUser1", now), // 차단된 사용자
            createCommentVo(5L, "blockedUser2", now)  // 차단된 사용자
        );
    }

    private GroupCommentVo createCommentVo(Long commentId, String nickname, LocalDateTime createdAt) {
        return new GroupCommentVo(
            commentId,
            false,
            false,
            nickname,
            "Test comment body",
            createdAt.toString()
        );
    }

    private void setupCommonMocks() {
        when(commentStrategyRegistry.getCommentGroupStrategy(any(GroupType.class)))
            .thenReturn(commentStrategy);
        when(userService.getUserById(testUser.getId()))
            .thenReturn(testUser);
        when(blockService.findUserBlocks(testUser.getId()))
            .thenReturn(blockedUsers);
        when(commentStrategy.readComment(testUser, true, readCommentRequest))
            .thenReturn(originalResponse);
    }
}
