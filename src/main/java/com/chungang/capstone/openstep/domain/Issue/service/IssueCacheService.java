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
    private static final String TRENDING_CACHE_KEY = "trending:issues";

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

    // 해시 저장
    public void saveInterestHash(Long memberId, String hash) {
        String key = generateInterestHashKey(memberId);
        redisTemplate.opsForValue().set(key, hash, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    // 해시 조회
    public String getInterestHash(Long memberId) {
        return redisTemplate.opsForValue().get(generateInterestHashKey(memberId));
    }

    // 해시 키 생성
    private String generateInterestHashKey(Long memberId) {
        return "recommend:issues:interest-hash:" + memberId;
    }

    // 필요 시 해시 강제 삭제 (옵션)
    public void evictInterestHash(Long memberId) {
        redisTemplate.delete(generateInterestHashKey(memberId));
    }


    public void saveTrendingIssues(List<Issue> issues) {
        try {
            String json = objectMapper.writeValueAsString(issues);
            redisTemplate.opsForValue().set(TRENDING_CACHE_KEY, json, 6, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("트렌딩 이슈 캐싱 실패", e);
        }
    }

    public List<Issue> getTrendingIssuesFromCache() {
        String cached = redisTemplate.opsForValue().get(TRENDING_CACHE_KEY);
        if (cached == null) return null;
        try {
            return objectMapper.readValue(cached, new TypeReference<List<Issue>>() {});
        } catch (Exception e) {
            throw new RuntimeException("트렌딩 이슈 캐시 역직렬화 실패", e);
        }
    }

    public void evictTrendingIssues() {
        redisTemplate.delete(TRENDING_CACHE_KEY);
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



}
