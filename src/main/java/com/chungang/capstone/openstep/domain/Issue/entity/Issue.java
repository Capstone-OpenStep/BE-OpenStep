package com.chungang.capstone.openstep.domain.Issue.entity;

import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Issue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    @JsonBackReference
    private Repo repo;

    @Column(columnDefinition = "varchar(500)", length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(unique = true)
    private String githubUrl;

    private String state; // OPEN, CLOSED 등

    private String author;

    private String authorAvatarUrl;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String language;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "issue_labels", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "label")
    private List<String> labels = new ArrayList<>();

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    private int stars;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        Issue issue = (Issue) o;
        return Objects.equals(githubUrl, issue.githubUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubUrl);
    }
}


