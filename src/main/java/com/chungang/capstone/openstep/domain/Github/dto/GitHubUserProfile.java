package com.chungang.capstone.openstep.domain.Github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubUserProfile {

    private String login;
    //private String name;
    private String email;
    private String avatarUrl;
    private String location;
    private String url;

    private int followersCount;
    private int followingCount;

    @JsonProperty("followers")
    private void unpackFollowers(Map<String, Integer> followers) {
        this.followersCount = followers.get("totalCount");
    }

    @JsonProperty("following")
    private void unpackFollowing(Map<String, Integer> following) {
        this.followingCount = following.get("totalCount");
    }
}
