package com.chungang.capstone.openstep.domain.Github.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitHubStatusResolverServiceImpl implements GitHubStatusResolverService {

    private final GitHubRestService gitHubRestService;

    @Override
    public TaskStatus resolveStatus(Task task, Member member) {
        String owner = task.getIssue().getRepo().getOwnerName();
        String repo = task.getIssue().getRepo().getRepoName();
        String user = member.getGithubId();
        String githubToken = member.getGithubAccessToken();

        // 1. 사용자가 레포를 포크했는지 확인
        if (!gitHubRestService.doesForkExist(user, repo,githubToken)) {
            return TaskStatus.NOT_STARTED;
        }

        // // 2. 포크 레포에 브랜치가 생성됐는지 확인 -> 브랜치 생성 일시를 파악할 수 없어 일단 버림
        // if (!gitHubRestService.hasNonDefaultBranch(user, repo,githubToken)) {
        //     return TaskStatus.FORKED;
        // }

        // 2. PR 생성 여부
        PullRequestResponse.PullRequestRes pr = gitHubRestService.findPullRequest(owner, repo, task.getBranchName(), user,githubToken);
        if (pr == null) {
            //pr null인 경우는 PR이 생성되지 않은 상태
            return TaskStatus.FORKED;
        }

        // 4. 리뷰 여부
        if (gitHubRestService.hasReview(owner, repo, pr.number(),githubToken)) {
            return TaskStatus.REVIEW;
        }

        // 5. 머지 or 반려 여부
        if (pr.mergedAt()!=null) {
            return TaskStatus.MERGED;
            }
        else if (Objects.equals(pr.state(), "closed")) {
            return TaskStatus.REJECTED;
        }

        return TaskStatus.PR; // PR 생성만 된 상태
    }
}