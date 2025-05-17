package com.chungang.capstone.openstep.domain.Github.dto;

import java.util.List;

public class PullRequestResponse {
	public record PullRequestRes(
		String title,
		int number,
		String state,
		String url,
		String createdAt,
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
		LabelWrapper labels
	) {
		public List<String> flatLabelNames() {
			if (labels == null || labels.nodes() == null) return List.of();
			return labels.nodes().stream()
				.map(LabelNode::name)
				.toList();
		}
	}
	record Author(String login) {}
	record LabelWrapper(
		List<LabelNode> nodes
	) {}

	record LabelNode(String name) {}
	record Repository(String nameWithOwner) {}
	record ClosingIssueWrapper(List<RelatedIssueDto> nodes) {}

}

