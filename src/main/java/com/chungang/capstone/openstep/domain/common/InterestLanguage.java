package com.chungang.capstone.openstep.domain.common;

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
}
