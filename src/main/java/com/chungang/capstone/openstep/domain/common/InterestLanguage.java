package com.chungang.capstone.openstep.domain.common;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum InterestLanguage {

    C("C"),
    CPP("C++"),
    C_SHARP("C#"),
    JAVA("Java"),
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    SWIFT("Swift"),
    KOTLIN("Kotlin"),
    PYTHON("Python"),
    RUST("Rust"),
    GO("Go"),
    R("R"),
    RUBY("Ruby"),
    PERL("Perl"),
    PHP("PHP"),
    SQL("SQL"),
    MATLAB("MATLAB"),
    SCRATCH("Scratch");

    private final String label;

    InterestLanguage(String label) {
        this.label = label;
    }

    public static InterestLanguage fromLabel(String label) {
        return Arrays.stream(InterestLanguage.values())
                .filter(lang -> lang.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 언어: " + label));
    }



}
