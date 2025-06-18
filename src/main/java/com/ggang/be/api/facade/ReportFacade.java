package com.ggang.be.api.facade;

import org.springframework.transaction.annotation.Transactional;

import com.ggang.be.api.comment.service.CommentService;
import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.group.dto.GroupResponse;
import com.ggang.be.api.group.facade.GroupFacade;
import com.ggang.be.api.report.dto.ReportCommentResponseDto;
import com.ggang.be.api.report.service.ReportService;
import com.ggang.be.api.user.service.UserService;
import com.ggang.be.domain.block.application.BlockServiceImpl;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.comment.infra.CommentRepository;
import com.ggang.be.domain.constant.GroupType;
import com.ggang.be.domain.report.ReportEntity;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.global.annotation.Facade;

import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class ReportFacade {

	private final CommentService commentService;
	private final CommentRepository commentRepository;
	private final UserService userService;
	private final ReportService reportService;
	private final BlockServiceImpl blockService;
	private final GroupFacade groupFacade;

	public ReportCommentResponseDto reportComment(long userId, long commentId) {

		CommentEntity commentEntity = commentRepository.findById(commentId)
			.orElseThrow(() -> new GongBaekException(ResponseError.NOT_FOUND));

		UserEntity userEntity = commentEntity.getUserEntity();
		long reportedId = userEntity.getId();

		// 신고를 하고 -> 댓글의 경우에는  바로 차단
		ReportEntity reportEntity = reportService.reportComment(commentId, userId, reportedId);

		blockService.blockUser(reportEntity, userEntity);

		return ReportCommentResponseDto.ok();
	}
}
