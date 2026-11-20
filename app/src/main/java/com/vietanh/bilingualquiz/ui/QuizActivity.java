package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.vietanh.bilingualquiz.LanguageSettings;
import com.vietanh.bilingualquiz.data.QuestionRepository;
import com.vietanh.bilingualquiz.data.QuizDbHelper;
import com.vietanh.bilingualquiz.model.Question;

public class QuizActivity extends Activity {
    int grade;
    int score = 0;
    Question question;
    LinearLayout root;
    TextView title, prompt;
    RadioGroup choices;
    Button submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grade = getIntent().getIntExtra("grade", 8);
        showQuestion();
    }

    private void showQuestion() {
        question = new QuestionRepository(this).getSampleQuestions(grade).get(0);
        boolean vietnamese = LanguageSettings.isVietnamese(this);
        root = AppUi.screen(this, (vietnamese ? "Ôn tập lớp " : "Practice grade ") + grade, true);
        AppUi.add(root, AppUi.text(this, vietnamese ? "Chọn một đáp án rồi nộp bài."
                : "Choose one answer, then submit.", 15, AppUi.MUTED, false), 8);

        LinearLayout questionCard = AppUi.card(this);
        AppUi.add(questionCard, AppUi.text(this, vietnamese ? "CÂU HỎI" : "QUESTION",
                12, AppUi.BLUE, true), 0);
        prompt = AppUi.text(this, vietnamese ? question.questionVi : question.questionEn,
                20, AppUi.TEXT, true);
        AppUi.add(questionCard, prompt, 12);
        AppUi.add(root, questionCard, 20);

        choices = new RadioGroup(this);
        int[][] tintStates = new int[][]{
                new int[]{android.R.attr.state_checked}, new int[]{}
        };
        ColorStateList radioTint = new ColorStateList(tintStates,
                new int[]{AppUi.BLUE, AppUi.MUTED});
        for (int index = 0; index < question.options.length; index++) {
            RadioButton choice = new RadioButton(this);
            choice.setId(View.generateViewId());
            choice.setText(question.getOption(index, vietnamese));
            choice.setTextSize(16);
            choice.setTextColor(AppUi.TEXT);
            choice.setButtonTintList(radioTint);
            choice.setMinHeight(AppUi.dp(this, 56));
            choice.setPadding(AppUi.dp(this, 12), AppUi.dp(this, 12),
                    AppUi.dp(this, 12), AppUi.dp(this, 12));
            StateListDrawable background = new StateListDrawable();
            background.addState(new int[]{android.R.attr.state_checked},
                    AppUi.background(this, AppUi.LIGHT_BLUE, 14));
            background.addState(new int[]{}, AppUi.background(this, AppUi.SURFACE, 14));
            choice.setBackground(background);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = AppUi.dp(this, 10);
            choices.addView(choice, params);
        }
        AppUi.add(root, choices, 8);

        submit = AppUi.button(this, vietnamese ? "Nộp bài" : "Submit", true);
        submit.setOnClickListener(view -> submitAnswer());
        AppUi.add(root, submit, 24);
    }

    private void submitAnswer() {
        int checkedId = choices.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, LanguageSettings.isVietnamese(this)
                    ? "Hãy chọn một đáp án trước." : "Choose an answer first.", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selected = findViewById(checkedId);
        int selectedIndex = choices.indexOfChild(selected);
        boolean correct = question.isCorrect(selectedIndex);
        score = correct ? 1 : 0;

        QuizDbHelper database = new QuizDbHelper(this);
        database.saveAttempt(grade, score, 1);
        if (!correct) {
            database.saveWrongQuestion(question.id, grade, question.questionVi,
                    question.questionEn, question.getCorrectAnswer(LanguageSettings.isVietnamese(this)));
        }
        Toast.makeText(this, (LanguageSettings.isVietnamese(this)
                ? "Đã lưu kết quả: " : "Result saved: ") + score + "/1", Toast.LENGTH_LONG).show();
        finish();
    }
}





