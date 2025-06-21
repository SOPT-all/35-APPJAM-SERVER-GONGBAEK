package com.ggang.be.api.facade;

import com.ggang.be.api.common.ResponseError;
import com.ggang.be.api.exception.GongBaekException;
import com.ggang.be.api.group.dto.GroupCreatorVo;
import com.ggang.be.api.group.facade.GroupFacade;
import com.ggang.be.api.report.dto.ReportCommentResponse;
import com.ggang.be.api.report.dto.ReportGroupResponse;
import com.ggang.be.api.report.service.ReportService;
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

	private final CommentRepository commentRepository;
	private final ReportService reportService;
	private final BlockServiceImpl blockService;
	private final GroupFacade groupFacade;

	public ReportCommentResponse reportComment(long userId, long commentId) {

		CommentEntity commentEntity = commentRepository.findById(commentId)
			.orElseThrow(() -> new GongBaekException(ResponseError.NOT_FOUND));

		UserEntity userEntity = commentEntity.getUserEntity();
		long reportedId = userEntity.getId();

		ReportEntity reportEntity = reportService.reportComment(commentId, userId, reportedId);

		blockService.blockUser(reportEntity, userEntity);

		return ReportCommentResponse.create();
	}


	public ReportGroupResponse reportGroup(long userId, long groupId, GroupType groupType){

		GroupCreatorVo groupCreator = groupFacade.findGroupCreator(groupType, groupId);

		reportService.reportGroup(groupId, userId, groupCreator.creatorId(), groupType);

		return ReportGroupResponse.create();
	}

}
