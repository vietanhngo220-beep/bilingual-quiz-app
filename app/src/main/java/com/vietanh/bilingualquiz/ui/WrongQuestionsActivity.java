package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Button;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.LinearLayout;
import com.vietanh.bilingualquiz.data.QuizDbHelper;
import com.vietanh.bilingualquiz.LanguageSettings;
import com.vietanh.bilingualquiz.data.QuestionRepository;
import com.vietanh.bilingualquiz.model.Question;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WrongQuestionsActivity extends Activity {
    private static final String SELECTED_GRADE = "selectedGrade";
    private boolean vi;
    private int selectedGrade;
    private LinearLayout questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vi = LanguageSettings.isVietnamese(this);
        selectedGrade = savedInstanceState == null ? 0
                : savedInstanceState.getInt(SELECTED_GRADE, 0);
        LinearLayout screen = AppUi.screen(this, vi ? "Câu hỏi sai" : "Wrong questions", true);
        LinearLayout filterCard = AppUi.card(this);
        AppUi.add(filterCard, AppUi.text(this, vi ? "Lọc theo lớp" : "Filter by grade",
                14, AppUi.MUTED, true), 0);
        AppUi.add(screen, filterCard, 20);

        String[] grades = new String[13];
        grades[0] = vi ? "Tất cả lớp" : "All grades";
        for (int grade = 1; grade <= 12; grade++) {
            grades[grade] = (vi ? "Lớp " : "Grade ") + grade;
        }
        Spinner gradeFilter = new Spinner(this);
        gradeFilter.setContentDescription(vi ? "Lọc theo lớp" : "Filter by grade");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, grades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeFilter.setAdapter(adapter);
        gradeFilter.setSelection(selectedGrade);
        gradeFilter.setMinimumHeight(AppUi.dp(this, 48));
        AppUi.add(filterCard, gradeFilter, 4);

        questionList = new LinearLayout(this);
        questionList.setOrientation(LinearLayout.VERTICAL);
        AppUi.add(screen, questionList, 4);
        gradeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = position;
                showWrongQuestions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        showWrongQuestions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_GRADE, selectedGrade);
        super.onSaveInstanceState(outState);
    }

    private void showWrongQuestions() {
        questionList.removeAllViews();
        QuizDbHelper database = new QuizDbHelper(this);
        Map<Integer, List<Question>> sourceByGrade = new HashMap<>();
        QuestionRepository repository = new QuestionRepository(this);
        try (Cursor cursor = database.getWrongQuestions(selectedGrade)) {
            int number = 1;
            while (cursor.moveToNext()) {
                LinearLayout card = AppUi.card(this);
                AppUi.add(card, AppUi.text(this,
                        number + ". " + (vi ? "Lớp " : "Grade ") + cursor.getInt(1),
                        14, AppUi.BLUE, true), 0);
                AppUi.add(card, AppUi.text(this,
                        vi ? cursor.getString(2) : cursor.getString(3),
                        18, AppUi.TEXT, true), 12);
                String answer = getSavedAnswer(cursor, repository, sourceByGrade);
                String explanation = getSavedExplanation(cursor, repository, sourceByGrade);
                String questionText = vi ? cursor.getString(2) : cursor.getString(3);
                Button details = AppUi.button(this,
                        vi ? "Xem đáp án và giải thích" : "View answer and explanation", false);
                details.setOnClickListener(view -> new AlertDialog.Builder(this)
                        .setTitle(vi ? "Chi tiết câu sai" : "Wrong question details")
                        .setMessage(questionText + "\n\n"
                                + (vi ? "Đáp án đúng: " : "Correct answer: ") + answer
                                + "\n\n" + (vi ? "Giải thích: " : "Explanation: ") + explanation)
                        .setPositiveButton(vi ? "Đóng" : "Close", null).show());
                AppUi.add(card, details, 16);
                AppUi.add(questionList, card, 12);
                number++;
            }

            if (number == 1) {
                LinearLayout empty = AppUi.card(this);
                AppUi.add(empty, AppUi.text(this, selectedGrade == 0
                        ? (vi ? "Chưa có câu sai được lưu.\nHãy hoàn thành một bài luyện tập trước."
                                : "No wrong questions saved.\nComplete a practice quiz first.")
                        : (vi ? "Chưa có câu sai được lưu cho lớp " + selectedGrade + "."
                                : "No wrong questions saved for grade " + selectedGrade + "."),
                        16, AppUi.MUTED, false), 0);
                AppUi.add(questionList, empty, 12);
            }
        } finally {
            database.close();
        }
    }

    private String getSavedExplanation(Cursor cursor, QuestionRepository repository,
                                       Map<Integer, List<Question>> sourceByGrade) {
        String saved = cursor.getString(vi ? 7 : 8);
        if (saved != null && !saved.trim().isEmpty()) return saved;
        int grade = cursor.getInt(1);
        if (!sourceByGrade.containsKey(grade)) {
            sourceByGrade.put(grade, repository.getSampleQuestions(grade));
        }
        for (Question source : sourceByGrade.get(grade)) {
            if (source.id == cursor.getInt(0) && source.grade == grade
                    && Objects.equals(source.questionVi, cursor.getString(2))
                    && Objects.equals(source.questionEn, cursor.getString(3))
                    && (Objects.equals(source.getCorrectAnswer(true), cursor.getString(5))
                        || Objects.equals(source.getCorrectAnswer(false), cursor.getString(6))
                        || Objects.equals(source.getCorrectAnswer(), cursor.getString(4))
                        || Objects.equals(source.getCorrectAnswer(true), cursor.getString(4))
                        || Objects.equals(source.getCorrectAnswer(false), cursor.getString(4)))) {
                String explanation = source.getExplanation(vi);
                if (!explanation.trim().isEmpty()) return explanation;
            }
        }
        return vi ? "Chưa có giải thích bằng tiếng Việt cho câu hỏi này."
                : "No English explanation is available for this question.";
    }

    private String getSavedAnswer(Cursor cursor, QuestionRepository repository,
                                  Map<Integer, List<Question>> sourceByGrade) {
        String translated = cursor.getString(vi ? 5 : 6);
        if (translated != null && !translated.isEmpty()) {
            return translated;
        }

        // Version 1 stored a single language. Recover only from the same question and answer.
        String legacy = cursor.getString(4);
        int grade = cursor.getInt(1);
        if (!sourceByGrade.containsKey(grade)) {
            sourceByGrade.put(grade, repository.getSampleQuestions(grade));
        }
        for (Question source : sourceByGrade.get(grade)) {
            if (source.id == cursor.getInt(0) && source.grade == grade
                    && Objects.equals(source.questionVi, cursor.getString(2))
                    && Objects.equals(source.questionEn, cursor.getString(3))
                    && legacy != null
                    && (legacy.equals(source.getCorrectAnswer())
                            || legacy.equals(source.getCorrectAnswer(true))
                            || legacy.equals(source.getCorrectAnswer(false)))) {
                return source.getCorrectAnswer(vi);
            }
        }
        // Keep unknown old data intact instead of guessing a translation.
        return legacy == null ? "" : legacy;
    }
}
