package com.chungang.capstone.openstep.domain.Issue.service;

import com.chungang.capstone.openstep.domain.Rank.entity.TaskXpLog;
import com.chungang.capstone.openstep.domain.Rank.repository.TaskXpLogRepository;
import com.chungang.capstone.openstep.domain.Rank.service.RankCommandService;
import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.service.GithubRepoService;
import com.chungang.capstone.openstep.domain.Issue.converter.IssueConverter;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Issue.repository.IssueRepository;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Task.converter.TaskConverter;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueCommandService {
	private final IssueRepository issueRepository;
	private final TaskRepository taskRepository;
	private final MemberRepository memberRepository;
	private final IssueQueryService issueQueryService;
	private final GithubRepoService githubRepoService;
	private final RankCommandService rankCommandService;
	private final TaskXpLogRepository taskXpLogRepository;

	//특정 이슈를 특정 멤버에게 할당하는 메서드
	public IssueResponseDTO.IssueAssignmentDTO makeTask(Member member,Long issueId) {
		Issue issue=issueQueryService.getIssueById(issueId);
		String url=issue.getGithubUrl();
		String[] urlParts=url.split("/");
		String repoName=urlParts[4];
		String repoOwner=urlParts[3];
		String issueNumber=urlParts[6];
		//todo:만약 이미 할당된 이슈라면 응답에 플래그 제공(이미 포크된 레포지토리)

		Task existingTask=taskRepository.findByMemberAndIssue(member,issue);
		if(existingTask!=null) {
			//이미 할당된 이슈라면 예외 or 기존 Task 반환?
			return IssueConverter.toIssueAssignDTO(existingTask,true);
		}

		githubRepoService.forkRepository(repoOwner,repoName,member.getGithubAccessToken());
		//2. 브랜치 이름 생성
		String branchName="feature/#"+issueNumber;
		Task task=Task.builder()
			.issue(issue)
			.member(member)
			.status(TaskStatus.FORKED)
			.forkedUrl("https://github.com/"+member.getGithubId()+"/"+repoName)
			.branchName(branchName)
			.build();
		Task savedTask=taskRepository.save(task);

		if (!taskXpLogRepository.existsByTaskAndStatus(savedTask, TaskStatus.FORKED)) {
			rankCommandService.addXp(member, TaskStatus.FORKED.getXp());

			TaskXpLog log = TaskXpLog.builder()
					.task(savedTask)
					.member(member)
					.status(TaskStatus.FORKED)
					.xpGranted(true)
					.grantedAt(LocalDateTime.now())
					.build();
			taskXpLogRepository.save(log);
		}

		return IssueConverter.toIssueAssignDTO(task,false);
	}
}
