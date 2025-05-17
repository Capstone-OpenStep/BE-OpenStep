package com.chungang.capstone.openstep.domain.Github.service;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;

public interface GitHubRestService {
    boolean doesForkExist(String user, String repo,String accessToken);
    // boolean hasNonDefaultBranch(String user, String repo,String accessToken);
    PullRequestResponse.PullRequestRes findPullRequest(String owner, String repo, String headRefName, String githubId,String accessToken);
    boolean hasReview(String owner, String repo, int prNumber,String accessToken);
}