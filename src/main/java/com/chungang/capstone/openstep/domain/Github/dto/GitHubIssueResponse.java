package com.chungang.capstone.openstep.domain.Github.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class GitHubIssueResponse {
    private Data data;

    @Getter
    public static class Data {
        private Repository repository;
        private Search search;
    }

    @Getter
    public static class Search {
        private List<Edge> edges;
    }

    @Getter
    public static class Edge {
        private GitHubIssueResponse.IssueNode node;
    }

    @Getter
    public static class Repository {
        private String name;
        private Issues issues;
        private IssueNode issue;
        private Owner owner;
        private String nameWithOwner;
    }

    @Getter
    @Setter
    public static class Issues {
        private List<IssueNode> nodes;
        private int totalCount;
    }

    @Getter
    public static class IssueNode {
        private String title;
        private String body;
        private String url;
        private String repo;
        private Author author;
        private Labels labels;
        private String state;
        private Language primaryLanguage;
        private Issues goodFirstIssue;
        private int stargazerCount;
        private String createdAt;
        private String updatedAt;

        @JsonIgnore
        private Repository repository;
        @JsonProperty("repository")
        private RepoInfo repoInfo;

        public int getGoodFirstIssueCount() {
            return goodFirstIssue != null ? goodFirstIssue.getTotalCount() : 0;
        }

    }

    @Getter
    public static class Author {
        private String login;
        private String avatarUrl;
    }

    @Getter
    public static class Labels {
        private List<LabelNode> nodes;
    }

    @Getter
    public static class LabelNode {
        private String name;
    }

    @Getter
    public static class Language {
        private String name;
    }

    @Getter
    public static class Owner {
        private String login;
        private String avatarUrl;
    }

    @Getter
    public static class RepoInfo {
        private String name;
        private String nameWithOwner;
        private Owner owner;
    }
}
