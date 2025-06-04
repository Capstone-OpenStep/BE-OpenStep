package com.chungang.capstone.openstep.domain.Github.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.chungang.capstone.openstep.domain.Github.dto.*;
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
                    avatarUrl
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
        issues(first: 2, orderBy: {field: UPDATED_AT, direction: DESC}) {
          nodes {
            title
            body
            url
            createdAt
            updatedAt
            author { 
                login 
                avatarUrl
            }
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
                  name
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
                  owner {
                    login
                    avatarUrl 
                  }
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

    // 키워드 이슈 검색
//    public GitHubIssueResponse searchIssues(String query) {
//        String finalQuery = String.format("""
//        {
//          search(query: "%s", type: ISSUE, first: 30) {
//            edges {
//              node {
//                ... on Issue {
//                  title
//                  body
//                  url
//                  createdAt
//                  updatedAt
//                  author {
//                    login
//                    avatarUrl
//                  }
//                  labels(first: 20) {
//                    nodes { name }
//                  }
//                }
//              }
//            }
//          }
//        }
//    """, query.replace("\"", "\\\""));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(githubToken);
//
//        HttpEntity<GitHubGraphQLRequest> request =
//                new HttpEntity<>(new GitHubGraphQLRequest(finalQuery), headers);
//
//        try {
//            ResponseEntity<GitHubIssueResponse> response = restTemplate.exchange(
//                    GITHUB_GRAPHQL_URL, HttpMethod.POST, request, GitHubIssueResponse.class
//            );
//
//            return response.getBody();
//        } catch (Exception e) {
//            log.error("GitHub 이슈 검색 실패: {}", e.getMessage(), e);
//            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
//        }
//    }

    public GitHubIssueResponse searchIssues(String query) {
        String finalQuery = String.format("""
        {
          search(query: \"%s\", type: ISSUE, first: 20) {
            edges {
              node {
                ... on Issue {
                  number
                  title
                  body
                  url
                  state
                  createdAt
                  updatedAt
                  author {
                    login
                    avatarUrl
                  }
                  labels(first: 10) {
                    nodes { name }
                  }
                  repository {
                    name
                    nameWithOwner
                  }
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
            ResponseEntity<GitHubIssueResponse> response = restTemplate.exchange(
                    GITHUB_GRAPHQL_URL, HttpMethod.POST, request, GitHubIssueResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("GitHub 이슈 검색 실패", e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }


    public GitHubIssueResponse.IssueNode fetchIssueByUrl(String url) {
        // 예: https://github.com/owner/repo/issues/123
        try {
            String[] parts = url.split("/");
            String owner = parts[3];
            String repo = parts[4];
            int number = Integer.parseInt(parts[6]);

            String query = String.format("""
        {
          repository(owner: "%s", name: "%s") {
            issue(number: %d) {
              number
              title
              body
              url
              state
              createdAt
              updatedAt
              author {
                login
                avatarUrl
              }
              labels(first: 10) {
                nodes {
                  name
                }
              }
              repository {
                name
                nameWithOwner
                owner {
                  login
                  avatarUrl
                }
              }
            }
          }
        }
        """, owner, repo, number);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(githubToken);

            HttpEntity<GitHubGraphQLRequest> request = new HttpEntity<>(new GitHubGraphQLRequest(query), headers);

            ResponseEntity<GitHubIssueResponse> response = restTemplate.exchange(
                    GITHUB_GRAPHQL_URL, HttpMethod.POST, request, GitHubIssueResponse.class
            );

            GitHubIssueResponse.Repository repoRes = response.getBody().getData().getRepository();
            if (repoRes != null) return repoRes.getIssue();
            else return null;

        } catch (Exception e) {
            log.error("[GraphQL] 이슈 단건 조회 실패", e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }


    public GitHubUserProfile fetchAuthenticatedUserProfile(String accessToken) {
        String query = """
        {
          viewer {
            login
            avatarUrl
            email
            location
            followers { totalCount }
            following { totalCount }
            url
          }
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<GitHubGraphQLRequest> request = new HttpEntity<>(new GitHubGraphQLRequest(query), headers);

        try {
            ResponseEntity<GitHubUserProfileResponse> response = restTemplate.exchange(
                    GITHUB_GRAPHQL_URL, HttpMethod.POST, request, GitHubUserProfileResponse.class
            );

            return Optional.ofNullable(response.getBody())
                    .map(GitHubUserProfileResponse::getData)
                    .map(GitHubUserProfileResponse.ViewerData::getViewer)
                    .orElseThrow(() -> new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR));

        } catch (Exception e) {
            log.error("GitHub 프로필 정보 조회 실패", e);
            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
        }
    }




}
