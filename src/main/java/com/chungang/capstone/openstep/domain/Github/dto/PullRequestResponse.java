package com.chungang.capstone.openstep.domain.Github.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PullRequestResponse {
	public record PullRequestRes(
		String title,
		int number,
		String state,

		@JsonProperty("html_url")
		String url,

		@JsonProperty("created_at")
		String createdAt,

		@JsonProperty("merged_at")
		String mergedAt,
		Repository repository,
		ClosingIssueWrapper closingIssuesReferences
	) { }

	public record RelatedIssueDto(
		int number,
		String title,
		String url,
		String createdAt,
		Author author,
		String authorAvatarUrl,
		LabelWrapper labels
	) {
	}
	record Author(String login) {}
	record LabelWrapper(
		List<LabelNode> nodes
	) {}

	record LabelNode(String name) {}
	record Repository(String nameWithOwner) {}
	record ClosingIssueWrapper(List<RelatedIssueDto> nodes) {}

}

