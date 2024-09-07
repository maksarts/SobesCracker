package ru.maksarts.sobescracker.model;

import java.util.stream.Stream;

public enum QuestionGrade {
    JUNIOR (100), MIDDLE (200), SENIOR (300);

    private int val;
    QuestionGrade(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static QuestionGrade of(int val) {
        return Stream.of(QuestionGrade.values())
                .filter(s -> s.getVal() == val)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid grade value: [%s] grade does not exist", val)));
    }
}
