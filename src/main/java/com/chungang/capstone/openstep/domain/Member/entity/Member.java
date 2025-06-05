package com.chungang.capstone.openstep.domain.Member.entity;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Rank.entity.Rank;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.achievement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;
import com.chungang.capstone.openstep.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String githubId;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    private String githubAccessToken;

    private String nickname;

    private String profileImageUrl;

    private int level;

    private int xp;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberDomain> domains = new ArrayList<>();

    private int projectExperience;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rank> ranks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberAchievement> achievements = new ArrayList<>();

    public List<MemberAchievement> getUnLockedAchievements() {
        return achievements.stream()
            .filter(MemberAchievement::isUnlocked)
            .toList();
    }

    public Optional<MemberAchievement> getAchievement(AchievementType type) {
        return achievements.stream()
            .filter(achievement -> achievement.getType() == type)
            .findFirst();
    }

    public boolean hasUnlockedAchievement(AchievementType type) {
        return getAchievement(type)
            .map(MemberAchievement::isUnlocked)
            .orElse(false);
    }

    public int getUnlockedAchievementCount() {
        return (int)achievements.stream()
            .filter(MemberAchievement::isUnlocked)
            .count();
    }

    public void updateGithubAccessToken(String githubAccessToken) {
        this.githubAccessToken = githubAccessToken;
    }
}

