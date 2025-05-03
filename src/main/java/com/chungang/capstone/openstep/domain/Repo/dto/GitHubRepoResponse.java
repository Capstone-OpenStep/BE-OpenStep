package com.chungang.capstone.openstep.domain.Repo.dto;

import lombok.Getter;
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
