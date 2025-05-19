package com.chungang.capstone.openstep.domain.Issue.service;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IssueCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private final long CACHE_EXPIRE_SECONDS = 60L * 60 * 24 * 60; // TTL 60일

    // 사용자별 추천 캐시 (기존 유지)
    public void saveRecommendedIssues(Long memberId, List<Issue> issues) {
        String key = generateMemberKey(memberId);
        try {
            String json = objectMapper.writeValueAsString(issues);
            redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 중 오류 발생", e);
        }
    }

    public List<Issue> getRecommendedIssues(Long memberId) {
        String key = generateMemberKey(memberId);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;

        try {
            return objectMapper.readValue(cached, new TypeReference<List<Issue>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Redis 조회 중 역직렬화 실패", e);
        }
    }

    // 새로운 관심 언어 + 관심 도메인 조합 캐시
    public void saveIssuesByLanguageAndDomain(String lang, String domain, List<Issue> issues) {
        String key = generateInterestKey(lang, domain);
        try {
            String json = objectMapper.writeValueAsString(issues);
            redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("관심조합 Redis 저장 중 오류 발생", e);
        }
    }

    public List<Issue> getIssuesByLanguageAndDomain(String lang, String domain) {
        String key = generateInterestKey(lang, domain);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;

        try {
            return objectMapper.readValue(cached, new TypeReference<List<Issue>>() {});
        } catch (Exception e) {
            throw new RuntimeException("관심조합 Redis 역직렬화 실패", e);
        }
    }

    public void evict(Long memberId) {
        redisTemplate.delete(generateMemberKey(memberId));
    }

    private String generateMemberKey(Long memberId) {
        return "recommend:issues:user:" + memberId;
    }

    private String generateInterestKey(String lang, String domain) {
        return "recommend:issues:interest:" + lang.toLowerCase() + ":" + domain.toLowerCase();
    }

    public void saveIssuesByLanguage(String lang, List<Issue> issues) {
        String key = "recommend:issues:lang:" + lang.toLowerCase();
        try {
            String json = objectMapper.writeValueAsString(issues);
            redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("언어 기반 Redis 저장 중 오류 발생", e);
        }
    }

    public void saveIssuesByDomain(String domain, List<Issue> issues) {
        String key = "recommend:issues:domain:" + domain.toLowerCase();
        try {
            String json = objectMapper.writeValueAsString(issues);
            redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("도메인 기반 Redis 저장 중 오류 발생", e);
        }
    }

    public List<Issue> getIssuesByLanguage(String lang) {
        String key = "recommend:issues:lang:" + lang.toLowerCase();
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;

        try {
            return objectMapper.readValue(cached, new TypeReference<List<Issue>>() {});
        } catch (Exception e) {
            throw new RuntimeException("언어 기반 Redis 역직렬화 실패", e);
        }
    }

    public List<Issue> getIssuesByDomain(String domain) {
        String key = "recommend:issues:domain:" + domain.toLowerCase();
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;

        try {
            return objectMapper.readValue(cached, new TypeReference<List<Issue>>() {});
        } catch (Exception e) {
            throw new RuntimeException("도메인 기반 Redis 역직렬화 실패", e);
        }
    }

}
