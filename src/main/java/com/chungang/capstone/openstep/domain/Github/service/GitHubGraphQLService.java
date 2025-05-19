package com.chungang.capstone.openstep.domain.Github.service;

import java.util.List;
import java.util.Objects;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubGraphQLRequest;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse;
import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponseWrapper;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GithubGraphQLException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubGraphQLService {

    private final RestTemplate restTemplate;

    @Value("${github.token}")
    private String githubToken;

    private static final String GITHUB_GRAPHQL_URL = "https://api.github.com/graphql";

    // Repo 정보 조회
    public GitHubRepoResponse fetchTrendingRepositories() {
        String query = """
        {
          search(query: "stars:>10000 sort:stars-desc", type: REPOSITORY, first: 5) {
            edges {
              node {
                ... on Repository {
                  name
                  description
                  url
                  stargazerCount
                  primaryLanguage {
                    name
                  }
                  owner {
                    login
                  }
                  forkCount
                  openIssues: issues(states: OPEN) {
                    totalCount
                  }
                  closedIssues: issues(states: CLOSED) {
                    totalCount
                  }
                  goodFirstIssue: issues(first: 1) {
                    totalCount
                  }
                  watchers {
                    totalCount
                  }
                  updatedAt
                }
              }
            }
          }
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        GitHubGraphQLRequest requestBody = new GitHubGraphQLRequest(query);
        HttpEntity<GitHubGraphQLRequest> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<GitHubRepoResponse> response = restTemplate.exchange(
                    GITHUB_GRAPHQL_URL,
                    HttpMethod.POST,
                    request,
                    GitHubRepoResponse.class
            );

            log.info("GitHub 응답 성공: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("GitHub GraphQL 호출 실패", e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }

    // Issue 정보 조회
    public GitHubIssueResponse fetchIssuesByRepo(String owner, String name) {
        String query = String.format("""
    {
      repository(owner: "%s", name: "%s") {
        name
        issues(first: 10, orderBy: {field: UPDATED_AT, direction: DESC}) {
          nodes {
            title
            body
            url
            createdAt
            updatedAt
            author { login }
            labels(first: 10) {
              nodes { name }
            }
          }
        }
      }
    }
    """, owner, name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        HttpEntity<GitHubGraphQLRequest> request = new HttpEntity<>(new GitHubGraphQLRequest(query), headers);

        try {
            ResponseEntity<GitHubIssueResponse> response = restTemplate.exchange(
                    GITHUB_GRAPHQL_URL, HttpMethod.POST, request, GitHubIssueResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("GitHub 이슈 조회 실패: {} / {}", owner, name, e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }

    // 내 PR 및 연결된 이슈 정보 조회
    public List<PullRequestResponse.PullRequestRes> fetchMyPullRequestsWithIssues(String githubId) {
        String query = String.format("""
        {
          user(login: "%s") {
            pullRequests(first: 100, orderBy: {field: UPDATED_AT, direction: DESC}) {
              nodes {
                title
                number
                url
                createdAt
                mergedAt
                state
                repository {
                  nameWithOwner
                }
                closingIssuesReferences(first: 5) {
                  nodes {
                    number
                    title
                    url
                    createdAt
                    author {
                      login
                    }
                    labels(first: 10) {
                      nodes {
                        name
                      }
                    }
                  }
                }
              }
            }
          }
        }
        """, githubId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        HttpEntity<GitHubGraphQLRequest> request = new HttpEntity<>(new GitHubGraphQLRequest(query), headers);

        try {
            ResponseEntity<PullRequestResponseWrapper> response = restTemplate.exchange(
                GITHUB_GRAPHQL_URL, HttpMethod.POST, request, PullRequestResponseWrapper.class
            );
            return Objects.requireNonNull(response.getBody()).pullRequests();
        } catch (Exception e) {
            log.error("GitHub 내 PR 조회 실패: {}", githubId, e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }

    // 레포지토리 사용자 맞춤 추천을 위한 검색
    public GitHubRepoResponse searchRepositories(String query) {
        String finalQuery = String.format("""
        {
          search(query: "%s", type: REPOSITORY, first: 20) {
            edges {
              node {
                ... on Repository {
                  name
                  description
                  url
                  stargazerCount
                  primaryLanguage { name }
                  owner { login }
                  forkCount
                  openIssues: issues(states: OPEN) { totalCount }
                  closedIssues: issues(states: CLOSED) { totalCount }
                  beginnerIssues: issues(labels: ["good first issue", "help wanted", "beginner friendly"]) {
                            totalCount
                          }
                
                  watchers { totalCount }
                  updatedAt
                }
              }
            }
          }
        }
        """, query.replace("\"", "\\\""));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        HttpEntity<GitHubGraphQLRequest> request =
                new HttpEntity<>(new GitHubGraphQLRequest(finalQuery), headers);

        try {
            ResponseEntity<GitHubRepoResponse> response = restTemplate.exchange(
                    GITHUB_GRAPHQL_URL, HttpMethod.POST, request, GitHubRepoResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("GitHub 동적 레포 검색 실패", e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }


}
