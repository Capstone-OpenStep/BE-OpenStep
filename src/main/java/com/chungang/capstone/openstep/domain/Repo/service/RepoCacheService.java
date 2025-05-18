package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RepoCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private final long CACHE_EXPIRE_SECONDS = 60L * 60 * 24 * 60; // 60일 유지

    // 🔁 사용자별 추천 캐시 (기존 유지)
    public void saveRecommendedRepos(Long memberId, List<Repo> repos) {
        String key = generateMemberKey(memberId);
        try {
            String json = objectMapper.writeValueAsString(repos);
            redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 중 오류 발생", e);
        }
    }

    public List<Repo> getRecommendedRepos(Long memberId) {
        String key = generateMemberKey(memberId);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;

        try {
            return objectMapper.readValue(cached, new TypeReference<List<Repo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Redis 조회 중 역직렬화 실패", e);
        }
    }

    // ✅ 새로운 관심 언어 + 관심 도메인 조합 캐시
    public void saveReposByLanguageAndDomain(String lang, String domain, List<Repo> repos) {
        String key = generateInterestKey(lang, domain);
        try {
            String json = objectMapper.writeValueAsString(repos);
            redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("관심조합 Redis 저장 중 오류 발생", e);
        }
    }

    public List<Repo> getReposByLanguageAndDomain(String lang, String domain) {
        String key = generateInterestKey(lang, domain);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;

        try {
            return objectMapper.readValue(cached, new TypeReference<List<Repo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("관심조합 Redis 역직렬화 실패", e);
        }
    }

    public void evict(Long memberId) {
        redisTemplate.delete(generateMemberKey(memberId));
    }

    private String generateMemberKey(Long memberId) {
        return "recommend:repos:user:" + memberId;
    }

    private String generateInterestKey(String lang, String domain) {
        return "recommend:repos:interest:" + lang.toLowerCase() + ":" + domain.toLowerCase();
    }
}
