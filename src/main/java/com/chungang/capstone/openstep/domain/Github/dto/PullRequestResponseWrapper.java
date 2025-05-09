package com.chungang.capstone.openstep.domain.Github.dto;

import java.util.List;

public record PullRequestResponseWrapper(
	Data data
) {
	public List<PullRequestResponse.PullRequestRes> pullRequests() {
		return data.user.pullRequests.nodes;
	}

	public record Data(User user) {}
	public record User(PullRequests pullRequests) {}
	public record PullRequests(List<PullRequestResponse.PullRequestRes> nodes) {}
}