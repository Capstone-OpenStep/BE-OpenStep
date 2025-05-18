package com.chungang.capstone.openstep.domain.Repo.entity;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Repo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repo_id")
    private Long repoId;

    private String repoName;

    private String ownerName;

    @Column(length = 1000)
    private String description;

    private String language;

    private int stars;
    private int watchers;
    private int forks;
    private int openIssues;
    private int closedIssues;

    @Column(name = "good_first_issue_count")
    private int goodFirstIssueCount;

    @Column(name = "github_url", unique = true)
    private String githubUrl;


    private String readmeUrl;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime lastGithubUpdate;

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repo)) return false;
        Repo repo = (Repo) o;
        return Objects.equals(githubUrl, repo.githubUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubUrl);
    }




}

