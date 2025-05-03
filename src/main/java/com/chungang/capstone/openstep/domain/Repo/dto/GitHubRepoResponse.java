package com.chungang.capstone.openstep.domain.Repo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class GitHubRepoResponse {
    private Data data;

    @Getter
    public static class Data {
        private Search search;
    }

    @Getter
    public static class Search {
        private List<Edge> edges;
    }

    @Getter
    public static class Edge {
        private Node node;
    }

    @Getter
    public static class Node {
        private String name;
        private String description;
        private String url;
        private int stargazerCount;
        private Language primaryLanguage;
        private Owner owner;
        private Integer forkCount;
        private Issues openIssues;
        private Issues closedIssues;
        private Issues goodFirstIssue;
        public Watchers watchers;
        private String updatedAt;

        public Integer getForkCount() {
            return forkCount;
        }

        public int getWatchersCount() {
            return watchers != null ? watchers.totalCount : 0;
        }
        public int getOpenIssuesCount() {
            return openIssues != null ? openIssues.getTotalCount() : 0;
        }

        public int getClosedIssuesCount() {
            return closedIssues != null ? closedIssues.getTotalCount() : 0;
        }

        public int getGoodFirstIssueCount() {
            return goodFirstIssue != null ? goodFirstIssue.getTotalCount() : 0;
        }
    }

    @Getter
    public static class Watchers {
        private int totalCount;
    }

    @Getter
    @Setter
    public static class Issues {
        private int totalCount;
    }

    @Getter
    public static class Language {
        private String name;
    }

    @Getter
    public static class Owner {
        private String login;
    }
}
