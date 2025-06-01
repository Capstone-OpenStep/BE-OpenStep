package com.chungang.capstone.openstep.domain.common;

import java.time.OffsetDateTime;

public enum UpdatePeriod {
    ONE_WEEK("1주") {
        @Override
        public OffsetDateTime getThresholdTime() {
            return OffsetDateTime.now().minusWeeks(1);
        }
    },
    ONE_MONTH("1개월") {
        @Override
        public OffsetDateTime getThresholdTime() {
            return OffsetDateTime.now().minusMonths(1);
        }
    },
    THREE_MONTHS("3개월") {
        @Override
        public OffsetDateTime getThresholdTime() {
            return OffsetDateTime.now().minusMonths(3);
        }
    },
    ONE_YEAR("1년") {
        @Override
        public OffsetDateTime getThresholdTime() {
            return OffsetDateTime.now().minusYears(1);
        }
    };

    private final String label;

    UpdatePeriod(String label) {
        this.label = label;
    }

    public abstract OffsetDateTime getThresholdTime();

    public String getLabel() {
        return label;
    }
}
