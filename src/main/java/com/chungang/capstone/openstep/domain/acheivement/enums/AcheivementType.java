package com.chungang.capstone.openstep.domain.acheivement.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AcheivementType {

	//즉시 달성형
	FIRST_COMMIT("첫 커밋","처음으로 PR을 등록했습니다.",1),
	DOC_WRITER("문서 작성자","문서관련 변경을 진행한 PR을 작성했습니다.",2),
	BUG_HUNTER("버그 헌터","버그 관련 PR을 작성했습니다.",3),

	//누적형
	PR_MASTER("PR 마스터","PR을 5개 이상 작성했습니다.",4),
	EXPLORER_LV1("탐험가 Lv.1","2개 이상의 다른 repositry에서 task를 할당받았습니다." ,6),
	EXPLORER_LV2("탐험가 Lv.2","5개 이상의 다른 repositry에서 task를 할당받았습니다." ,7),
	EXPLORER_LV3("탐험가 Lv.3","10개 이상의 다른 repositry에서 task를 할당받았습니다." ,8),

	//연속형
	CONSISTENT_DEV("꾸준한 개발자","7일 연속으로 최소 1개의 TASK활동이 일어났습니다." ,5),

	//조건부 달성 형
	MENTOR("검증받은 자","작성한 PR에 3개 이상의 리뷰가 달렸고, 그 중 하나라도 GOOD,NICE,LGTM 코멘트가 포함되어있습니다." ,9);

	private final String title;
	private final String description;
	private final int targetCount;
}
