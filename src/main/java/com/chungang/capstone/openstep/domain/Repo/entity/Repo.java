package com.chungang.capstone.openstep.domain.Repo.entity;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String description;

    private String language;

    private int stars;

    private String githubUrl;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();
}
