package com.chungang.capstone.openstep.domain.Github.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GithubRestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GithubRepoService {

	private final RestTemplate restTemplate = new RestTemplate();

	public void forkRepository(String owner, String repoName, String accessToken) {
		String url = "https://api.github.com/repos/" + owner + "/" + repoName + "/forks";

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(accessToken);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			url,
			HttpMethod.POST,
			request,
			String.class
		);

		if (response.getStatusCode().is2xxSuccessful()) {
			log.info("Repository forked successfully: " + response.getBody());
		} else {
			throw new GithubRestException(ErrorStatus.GITHUB_REST_ERROR);
		}
	}
}
