package com.chungang.capstone.openstep.domain.Github.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse;
import com.chungang.capstone.openstep.domain.Github.dto.GithubBranch;
import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Github.service.GitHubRestService;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GithubRestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GithubRestServiceImpl implements GitHubRestService {


	@Override
	public boolean doesForkExist(String user, String repo, String accessToken) {
	    String url = "https://api.github.com/repos/" + user + "/" + repo;

	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setBearerAuth(accessToken);
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        ResponseEntity<String> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                entity,
	                String.class
	        );

	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(response.getBody());
	        boolean isFork = root.path("fork").asBoolean(false);
			String parentFullName = root.path("parent").path("full_name").asText("");


			// 여기서 원본 레포 이름을 실제 소스에서 받아오도록 바꿔야 함
	        return isFork && !parentFullName.isEmpty();

	    } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
	        return false;
	    } catch (Exception e) {
	        log.error("GitHub 포크 존재 확인 중 예외 발생", e);
	        throw new GithubRestException(ErrorStatus.GITHUB_REST_ERROR);
	    }
	}

	// @Override
	// public boolean hasNonDefaultBranch(String user, String repo,String accessToken) {
	// 	String url = "https://api.github.com/repos/" + user + "/" + repo + "/branches";
	// 	try {
	// 		RestTemplate restTemplate = new RestTemplate();
	// 		HttpHeaders headers = new HttpHeaders();
	//
	// 		headers.setBearerAuth(accessToken);
	// 		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	// 		HttpEntity<Void> entity = new HttpEntity<>(headers);
	//
	// 		ResponseEntity<GithubBranch[]> response = restTemplate.exchange(
	// 				url,
	// 				HttpMethod.GET,
	// 				entity,
	// 				GithubBranch[].class
	// 		);
	//
	// 		GithubBranch[] branches = response.getBody();
	// 		if (branches != null) {
	// 			for (GithubBranch branch : branches) {
	// 				if (!branch.getName().equals("main") && !branch.getName().equals("master")) {
	// 					return true; // 비기본 브랜치가 존재하는 경우
	// 				}
	// 			}
	// 		}
	//
	// 	} catch (Exception e) {
	// 		log.error("GitHub 브랜치 조회 중 예외 발생", e);
	// 		throw new GithubRestException(ErrorStatus.GITHUB_REST_ERROR);
	// 	}
	// 	return false;
	// }

	@Override
	public PullRequestResponse.PullRequestRes findPullRequest(String owner, String repo, String branchName, String githubId,String accessToken) {
	    // String url = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls?state=all&head=" + branchName;

		String encodedBranch = URLEncoder.encode(branchName, StandardCharsets.UTF_8);
		String urlStr = String.format(
			"https://api.github.com/repos/%s/%s/pulls?state=all&head=%s:%s",
			owner, repo, githubId, encodedBranch
		);

		URI uri = URI.create(urlStr);
		log.info("Direct URI creation: {}", uri.toString());
	    try {
	        RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();

			headers.setBearerAuth(accessToken);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	        HttpEntity<Void> entity = new HttpEntity<>(headers);

			log.info("Calling GitHub API: {}", uri);

	        ResponseEntity<PullRequestResponse.PullRequestRes[]> response = restTemplate.exchange(
	                uri,
	                HttpMethod.GET,
	                entity,
	                PullRequestResponse.PullRequestRes[].class
	        );

	        PullRequestResponse.PullRequestRes[] pullRequests = response.getBody();
	        if (pullRequests != null && pullRequests.length > 0) {
				log.info("Found {} PR(s) for branch: {}", pullRequests.length, branchName);
	            return pullRequests[0]; // PR은 보통 1개면 충분하므로 첫 번째 것 반환
	        } else {
				log.info("No PR found for branch: {}", branchName);
			}

	    } catch (Exception e) {
	        log.error("GitHub PR 조회 중 예외 발생", e);
			throw new GithubRestException(ErrorStatus.GITHUB_REST_ERROR);
	    }
		return null; // PR이 없거나 오류 발생 시 null 반환
	}

	@Override
	public boolean hasReview(String owner, String repo, int prNumber,String accessToken) {
		String url = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls/" + prNumber + "/reviews";
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();

			headers.setBearerAuth(accessToken);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			HttpEntity<Void> entity = new HttpEntity<>(headers);

			ResponseEntity<PullRequestResponse.PullRequestRes[]> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					entity,
					PullRequestResponse.PullRequestRes[].class
			);

			PullRequestResponse.PullRequestRes[] reviews = response.getBody();
			if (reviews != null) {
				for (PullRequestResponse.PullRequestRes review : reviews) {
					if (review.state().equals("APPROVED")) {
						return true; // 리뷰가 승인된 경우
					}
				}
			}

		} catch (Exception e) {
			log.error("GitHub PR 리뷰 조회 중 예외 발생", e);
			throw new GithubRestException(ErrorStatus.GITHUB_REST_ERROR);
		}
		return false;// 리뷰가 없거나 오류 발생 시 false 반환
	}

	private String buildPullRequestUrl(String owner, String repo, String githubId, String branchName) {
		try {
			//브랜치명 URL 인코딩
			String encodedBranch = URLEncoder.encode(branchName, StandardCharsets.UTF_8);

			return String.format(
				"https://api.github.com/repos/%s/%s/pulls?state=all&head=%s:%s",
				owner, repo, githubId, encodedBranch
			);
		} catch (Exception e) {
			log.error("Error encoding branch name: {}", branchName, e);
			return null;
		}
	}

}
