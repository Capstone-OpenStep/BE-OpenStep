package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.Repo.dto.GitHubGraphQLRequest;
import com.chungang.capstone.openstep.domain.Repo.dto.GitHubRepoResponse;
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

    public GitHubRepoResponse fetchTrendingRepositories() {
        String query = """
        {
          search(query: "stars:>10000 sort:stars-desc", type: REPOSITORY, first: 10) {
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
            return null;
        }
    }
}
