package com.chungang.capstone.openstep.domain.Github.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GitHubIssueResponse {
    private Data data;

    @Getter
    public static class Data {
        private Repository repository;
    }

    @Getter
    public static class Repository {
        private String name;
        private Issues issues;
    }

    @Getter
    public static class Issues {
        private List<IssueNode> nodes;
    }

    @Getter
    public static class IssueNode {
        private String title;
        private String body;
        private String url;
        private String createdAt;
        private String updatedAt;
        private Author author;
        private Labels labels;
    }

    @Getter
    public static class Author {
        private String login;
    }

    @Getter
    public static class Labels {
        private List<LabelNode> nodes;
    }

    @Getter
    public static class LabelNode {
        private String name;
    }


}
