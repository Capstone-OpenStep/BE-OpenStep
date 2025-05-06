package com.chungang.capstone.openstep.domain.OpenAI.service;

import com.chungang.capstone.openstep.global.config.OpenAIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate;

    public String summarizeRepo(String description, String readmeContent) {
        String content = description + "\n\n" + readmeContent;
        String prompt = """
                다음은 오픈소스 레포지토리의 설명과 README 내용입니다.
                주요 기능과 목적을 자연스럽고 부드러운 한국어로 2~3문장 정도로 요약해주세요.
                딱딱하거나 어색한 번역체 표현(예: "이 프로젝트는 ~입니다", "이 레포지토리는 ~합니다.") 대신,
                사람들이 실제로 쓸 법한 자연스러운 말투로 정리해주세요.
                """ + "\n" + content;

        return callOpenAI(prompt);
    }

    public String summarizeIssue(String title, String body) {
        String content = title + "\n\n" + body;
        String prompt = """
                다음은 오픈소스 이슈의 제목과 본문입니다.
                어떤 논의가 오갔는지, 어떤 문제를 다루고 있는지 한국어로 간단히 요약해주세요.
                부드럽고 읽기 쉬운 말투로 2~3문장 이내로 정리해주세요.
                딱딱한 표현은 피하고 자연스럽게 풀어 써주세요.
                오픈소스 프로젝트 기여 초보자가 쉽게 이해할 수 있도록 해주세요.
                """ + "\n" + content;

        return callOpenAI(prompt);
    }

    public String rewriteNaturalKorean(String summarizedText) {
        String prompt = String.format("""
        다음은 오픈소스 레포지토리나 이슈 내용을 요약한 문장입니다.
        이를 자연스럽고 부드러운 한국어 문장으로 다시 다듬어주세요.
        너무 딱딱하거나 리스트 형식처럼 보이지 않도록, 일반적인 글처럼 매끄럽게 풀어서 정리해주세요.
        (예: "이 프로젝트는 ~입니다", "이 레포지토리는 ~합니다.")의 표현은 피해주세요.
        이모티콘이나 이모지, 특수문자 사용은 피해주세요.
        
        ---
        [요약문]
        %s
        """, summarizedText);

        return callOpenAI(prompt);
    }


    private String callOpenAI(String prompt) {
        String endpoint = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(message),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
        } catch (Exception e) {
            log.error("GPT 호출 실패", e);
            return "요약 중 오류가 발생했습니다.";
        }
    }
}
