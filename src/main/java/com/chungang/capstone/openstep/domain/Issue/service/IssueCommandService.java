package com.chungang.capstone.openstep.domain.Issue.service;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.service.GithubRepoService;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Issue.repository.IssueRepository;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueCommandService {
	private final IssueRepository issueRepository;
	private final TaskRepository taskRepository;
	private final MemberRepository memberRepository;
	private final IssueQueryService issueQueryService;
	private final GithubRepoService githubRepoService;


	//특정 이슈를 특정 멤버에게 할당하는 메서드
	public Task makeTask(Member member,Long issueId) {
		Issue issue=issueQueryService.getIssueById(issueId);
		String url=issue.getGithubUrl();
		String[] urlParts=url.split("/");
		String repoName=urlParts[4];
		String repoOwner=urlParts[3];
		//todo:만약 이미 할당된 이슈라면 응답에 플래그 제공(이미 포크된 레포지토리)
		//레파지토리 포크 작동 지금 안함;;
		githubRepoService.forkRepository(repoOwner,repoName,member.getGithubAccessToken());
		Task task=Task.builder()
			.issue(issue)
			.member(member)
			.status(TaskStatus.FORKED)
			.forkedUrl("https://github.com/"+member.getGithubId()+"/"+repoName)
			.build();
		return taskRepository.save(task);

	}
}
