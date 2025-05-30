package com.chungang.capstone.openstep.domain.Github.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
public class GitHubQueryBuilder {

//    public static String buildSearchQuery(List<String> languages, List<String> domains) {
//        StringBuilder query = new StringBuilder();
//
//        if (!languages.isEmpty()) {
//            String langQuery = languages.stream()
//                    .map(lang -> "language:" + lang)
//                    .collect(Collectors.joining(" "));
//            query.append(langQuery);
//        }
//
//        if (!domains.isEmpty()) {
//            if (query.length() > 0) query.append(" ");
//            String domainQuery = domains.stream()
//                    .map(String::toLowerCase)
//                    .map(domain -> domain.replace(" ", "-")) // e.g. UI/UX → ui-ux
//                    .map(domain -> "topic:" + domain)
//                    .collect(Collectors.joining(" "));
//            query.append(domainQuery);
//        }
//
//        query.append(" sort:stars-desc"); // 인기순 정렬
//        log.info("GraphQL query: {}", query);
//
//        return query.toString().trim();
//    }
//
//
//    public static String buildBroadKeywordQuery(List<String> languages, List<String> domains) {
//        List<String> keywords = new ArrayList<>();
//        keywords.addAll(languages);
//        keywords.addAll(domains);
//
//        log.info("Broad keywords: {}", keywords);
//        return keywords.stream()
//                .map(k -> k.toLowerCase().replace("/", " ").replace("-", " "))
//                .collect(Collectors.joining(" ")) + " sort:stars-desc";
//
//    }

    // 언어와 도메인(토픽)을 기반으로 GitHub GraphQL 검색 쿼리를 생성
    public static String buildSearchQuery(List<String> languages, List<String> domains) {
        StringJoiner query = new StringJoiner(" ");

        for (String lang : languages) {
            query.add("language:" + lang.toLowerCase());
        }

        for (String domain : domains) {
            // topic은 띄어쓰기 대신 하이픈(-) 처리
            String topic = domain.toLowerCase().replaceAll("[\\s/]+", "-");
            query.add("topic:" + topic);
        }

        query.add("sort:stars-desc"); // 정렬 조건

        String finalQuery = query.toString();
        log.info("[*] Explicit GitHub search query: {}", finalQuery);
        return finalQuery;
    }

    public static String buildLooseSearchQuery(List<String> languages, List<String> domains) {
        List<String> parts = new ArrayList<>();

        for (String lang : languages) {
            parts.add("language:" + lang.toLowerCase());
        }

        for (String domain : domains) {
            String topic = domain.toLowerCase().replaceAll("[\\s/]+", "-");
            parts.add("topic:" + topic);
        }

        String query = parts.stream().collect(Collectors.joining(" OR ")) + " sort:stars-desc";

        log.info("[*] OR-based GitHub search query: {}", query);
        return query;
    }





}
