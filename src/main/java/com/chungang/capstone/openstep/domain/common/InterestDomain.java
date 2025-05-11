package com.chungang.capstone.openstep.domain.common;

import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum InterestDomain {

    FRONTEND("Frontend"),
    BACKEND("Backend"),
    SPRING_BOOT("Spring Boot"),
    REACT("React"),
    UI_UX("UI/UX"),
    DEVOPS("DevOps"),
    CLOUD("Cloud"),
    DOCKER("Docker"),
    DATABASE("Database"),
    MYSQL("MySQL"),
    AI("AI"),
    DEEP_LEARNING("Deep Learning"),
    MOBILE("Mobile"),
    SECURITY("Security"),
    EMBEDDED("Embedded"),
    GAME_DEV("Game Development"),
    BLOCKCHAIN("Blockchain"),
    DATA_SCIENCE("Data Science"),
    LINUX("Linux"),
    GRAPHQL("GraphQL");

    private final String label;

    InterestDomain(String label) {
        this.label = label;
    }

    public static InterestDomain fromLabel(String label) {
        return Arrays.stream(InterestDomain.values())
                .filter(domain -> domain.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도메인: " + label));
    }
}
