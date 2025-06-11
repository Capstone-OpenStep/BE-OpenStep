package com.chungang.capstone.openstep.domain.Github.service;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.GithubTaskInfo;
import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;

import lombok.RequiredArgsConstructor;

//github api 호출만 담당
@Service
@RequiredArgsConstructor
public class GithubInfoService {
	private final GitHubRestService githubRestService;

	public GithubTaskInfo getGithubTaskInfo(Task task, Member member) {
		String owner=task.getIssue().getRepo().getOwnerName();
		String repo=task.getIssue().getRepo().getRepoName();
		String user=member.getGithubId();
		String githubToken=member.getGithubAccessToken();

		//1. fork 존재 여부 확인
		boolean forkExist=githubRestService.doesForkExist(user, repo, githubToken);

		PullRequestResponse.PullRequestRes pr=null;
		boolean hasReview=false;

		if(forkExist) {
			//2. PR 존재 여부 확인
			pr=githubRestService.findPullRequest(owner, repo, task.getBranchName(), user, githubToken);
			if (pr != null && "open".equals(pr.state())) {
				hasReview = githubRestService.hasReview(
					owner, repo, pr.number(), githubToken);
			}
		}
		return GithubTaskInfo.builder()
			.forkExist(forkExist)
			.pullRequest(pr)
			.hasReview(hasReview)
			.build();
	}
}
