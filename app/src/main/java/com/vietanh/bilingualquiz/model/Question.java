package com.vietanh.bilingualquiz.model;

import java.util.Arrays;

/** Shared data structure for every bilingual multiple-choice question. */
public class Question {
    public static final int OPTION_COUNT = 4;

    public int id;
    public int grade;
    public String subject;
    public String questionVi;
    public String questionEn;
    public String[] options;
    public int correctIndex;
    public String explanation;

    public Question() {
        options = new String[OPTION_COUNT];
    }

    public Question(int id, int grade, String subject, String questionVi,
                    String questionEn, String[] options, int correctIndex,
                    String explanation) {
        this.id = id;
        this.grade = grade;
        this.subject = subject;
        this.questionVi = questionVi;
        this.questionEn = questionEn;
        this.options = options == null ? new String[OPTION_COUNT] : options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex >= 0 && selectedIndex == correctIndex;
    }

    public String getCorrectAnswer() {
        if (options == null || correctIndex < 0 || correctIndex >= options.length) {
            return "";
        }
        return options[correctIndex];
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", grade=" + grade +
                ", subject='" + subject + '\'' +
                ", options=" + Arrays.toString(options) +
                ", correctIndex=" + correctIndex +
                '}';
    }
}
