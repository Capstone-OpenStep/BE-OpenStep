package com.chungang.capstone.openstep.domain.Github.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GithubTaskInfo {
	private final boolean forkExist;
	private final PullRequestResponse.PullRequestRes pullRequest;
	private final boolean hasReview;

	public boolean hasPullRequest() {
		return pullRequest != null;
	}

	public String getPrUrl() {
		return hasPullRequest() ? pullRequest.url() : null;
	}
	public boolean isPullRequestMerged() {
		return hasPullRequest() && pullRequest.mergedAt() != null;
	}

	public boolean isPullRequestClosed() {
		return hasPullRequest() && "closed".equals(pullRequest.state());
	}

	public boolean isPullRequestOpen() {
		return hasPullRequest() && "open".equals(pullRequest.state());
	}
}
