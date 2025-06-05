package com.chungang.capstone.openstep.domain.achievement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AchievementType {

	// 즉시 달성형
	FIRST_COMMIT("첫 커밋", "처음으로 PR을 등록했을 때", 1, AchievementCategory.INSTANT),

	// 누적형
	BUG_HUNTER("버그 헌터", "bug, fix, error, issue 등의 키워드가 포함된 PR을 생성", 5, AchievementCategory.CUMULATIVE),
	PR_MASTER("PR 마스터", "총 PR 생성 횟수가 5회 이상", 5, AchievementCategory.CUMULATIVE),

	// 연속형 (활동 기반)
	CONSISTENT_DEV("꾸준한 개발자", "7일 연속으로 Task 활동(상태 변경, 업데이트 등)을 진행", 7, AchievementCategory.CONSECUTIVE),

	// 탐험형
	EXPLORER_LV1("탐험가 LV1", "2개 이상의 서로 다른 Repo에서 Task를 생성", 2, AchievementCategory.EXPLORATION),
	EXPLORER_LV2("탐험가 LV2", "5개 이상의 서로 다른 Repo에서 Task를 생성", 5, AchievementCategory.EXPLORATION),
	EXPLORER_LV3("탐험가 LV3", "10개 이상의 서로 다른 Repo에서 Task를 생성", 10, AchievementCategory.EXPLORATION),

	// 복합형
	MENTOR("멘토", "본인의 PR에 3건 이상의 리뷰 코멘트를 달았고, 그 중 하나라도 'Good', 'Nice', 'LGTM' 포함", 3, AchievementCategory.COMPLEX);

	private final String title;
	private final String description;
	private final int targetCount;
	private final AchievementCategory category;

	public boolean isInstantType() {
		return this.category == AchievementCategory.INSTANT;
	}

	public boolean needsConsecutiveCheck() {
		return this.category == AchievementCategory.CONSECUTIVE;
	}

	public boolean needsExplorationTracking() {
		return this.category == AchievementCategory.EXPLORATION;
	}
}
