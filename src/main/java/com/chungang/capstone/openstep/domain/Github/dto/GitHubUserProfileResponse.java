package com.chungang.capstone.openstep.domain.Github.dto;

import lombok.Data;

@Data
public class GitHubUserProfileResponse {
    private ViewerData data;

    @Data
    public static class ViewerData {
        private GitHubUserProfile viewer;
    }
}
