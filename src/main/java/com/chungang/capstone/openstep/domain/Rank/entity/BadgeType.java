package com.chungang.capstone.openstep.domain.Rank.entity;

public enum BadgeType {
    FIRST_COMMIT("첫 커밋", "첫 커밋을 생성한 기여자"),
    DOC_CONTRIBUTOR("문서 기여자", "문서화 기여로 도움을 준 유저"),
    BUG_HUNTER("버그 헌터", "버그 이슈를 처음으로 발견한 유저"),
    PR_MASTER("PR 장인", "10회 이상 Pull Request 성공"),
    CONSISTENT_DEV("지속성의 신", "3일 연속 커밋 성공"),
    EXPLORER_LV1("탐험가 Lv1", "3개의 다른 레포 기여"),
    EXPLORER_LV2("탐험가 Lv2", "6개의 다른 레포 기여"),
    EXPLORER_LV3("탐험가 Lv3", "9개의 다른 레포 기여"),
    MENTOR("멘토", "다른 사람의 이슈에 피드백 작성"),
    COLLAB_MASTER("협업 마스터", "여러 팀원과 협업 경험 다수");

    private final String label;
    private final String description;

    BadgeType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
