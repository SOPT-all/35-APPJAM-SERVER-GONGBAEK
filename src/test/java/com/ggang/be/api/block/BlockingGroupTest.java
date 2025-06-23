package com.ggang.be.api.block;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ggang.be.api.group.ActiveCombinedGroupVoPreparer;
import com.ggang.be.api.group.dto.CombinedGroupVos;
import com.ggang.be.api.group.everyGroup.service.EveryGroupService;
import com.ggang.be.api.group.onceGroup.service.OnceGroupService;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.group.everyGroup.EveryGroupEntity;
import com.ggang.be.domain.group.everyGroup.EveryGroupFixture;
import com.ggang.be.domain.group.everyGroup.dto.EveryGroupVo;
import com.ggang.be.domain.group.everyGroup.dto.ReadEveryGroup;
import com.ggang.be.domain.group.onceGroup.OnceGroupEntity;
import com.ggang.be.domain.group.onceGroup.OnceGroupFixture;
import com.ggang.be.domain.group.onceGroup.dto.OnceGroupVo;
import com.ggang.be.domain.group.onceGroup.dto.ReadOnceGroup;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.user.fixture.UserEntityFixture;

@ExtendWith(MockitoExtension.class)
class BlockingGroupTest {

	@Mock
	private OnceGroupService onceGroupService;

	@Mock
	private EveryGroupService everyGroupService;

	@InjectMocks
	private ActiveCombinedGroupVoPreparer activeCombinedGroupVoPreparer;

	private UserEntity testUser;
	private Category testCategory;
	private List<String> blockedUsers;

	private List<EveryGroupVo> everyGroupVos;
	private List<OnceGroupVo> onceGroupVos;

	@BeforeEach
	void setUp() {
		// 테스트 기본 데이터 설정
		testUser = UserEntityFixture.createByNickname("hello1");
		testCategory = Category.EXERCISE;
		blockedUsers = List.of("hello2");

		// 테스트 그룹 데이터 생성
		everyGroupVos = createEveryGroupTestData();
		onceGroupVos = createOnceGroupTestData();

		// 공통 Mock 설정
		setupCommonMocks();
	}

	@Test
	@DisplayName("차단된 사용자의 그룹이 필터링되어야 한다")
	void shouldFilterBlockedUserGroups() {
		// When
		CombinedGroupVos result = activeCombinedGroupVoPreparer.prepareGroupVos(
			testUser, testCategory, blockedUsers);

		// Then
		assertThat(result.everyGroupVos()).hasSize(2);
		assertThat(result.onceGroupVos()).hasSize(2);

		// 차단된 사용자(hello2)가 제외되었는지 확인
		assertThat(result.everyGroupVos())
			.extracting(EveryGroupVo::nickname)
			.doesNotContain("hello2")
			.contains("hello1", "hello3");

		assertThat(result.onceGroupVos())
			.extracting(OnceGroupVo::nickname)
			.doesNotContain("hello2")
			.contains("hello1", "hello3");
	}

	@Test
	@DisplayName("차단 리스트가 비어있으면 모든 그룹이 반환되어야 한다")
	void shouldReturnAllGroupsWhenNoBlockedUsers() {
		// Given
		List<String> emptyBlockList = List.of();

		// When
		CombinedGroupVos result = activeCombinedGroupVoPreparer.prepareGroupVos(
			testUser, testCategory, emptyBlockList);

		// Then
		assertThat(result.everyGroupVos()).hasSize(3);
		assertThat(result.onceGroupVos()).hasSize(3);
	}

	private List<EveryGroupVo> createEveryGroupTestData() {
		List<EveryGroupEntity> entities = List.of(
			createEveryGroupEntityWithId("hello1", 1L),
			createEveryGroupEntityWithId("hello2", 2L),
			createEveryGroupEntityWithId("hello3", 3L)
		);

		return entities.stream()
			.map(EveryGroupVo::of)
			.toList();
	}

	private List<OnceGroupVo> createOnceGroupTestData() {
		List<OnceGroupEntity> entities = List.of(
			createOnceGroupEntityWithId("hello1", 1L),
			createOnceGroupEntityWithId("hello2", 2L),
			createOnceGroupEntityWithId("hello3", 3L)
		);

		return entities.stream()
			.map(OnceGroupVo::of)
			.toList();
	}

	private EveryGroupEntity createEveryGroupEntityWithId(String nickname, Long id) {
		EveryGroupEntity entity = EveryGroupFixture.createByUserNickname(nickname);
		ReflectionTestUtils.setField(entity, "id", id);
		return entity;
	}

	private OnceGroupEntity createOnceGroupEntityWithId(String nickname, Long id) {
		OnceGroupEntity entity = OnceGroupFixture.createByUserNickname(nickname);
		ReflectionTestUtils.setField(entity, "id", id);
		return entity;
	}

	private void setupCommonMocks() {
		when(everyGroupService.isSameSchoolEveryGroup(any(UserEntity.class), any(EveryGroupVo.class)))
			.thenReturn(true);
		when(onceGroupService.isSameSchoolOnceGroup(any(UserEntity.class), any(OnceGroupVo.class)))
			.thenReturn(true);

		// 서비스 호출 Mock
		when(everyGroupService.getActiveEveryGroups(testUser, testCategory))
			.thenReturn(ReadEveryGroup.of(everyGroupVos));
		when(onceGroupService.getActiveOnceGroups(testUser, testCategory))
			.thenReturn(ReadOnceGroup.of(onceGroupVos));
	}
}