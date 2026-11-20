package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
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
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);

        title = new TextView(this);
        title.setText("Ôn tập lớp " + grade + " / Practice grade " + grade);
        title.setTextSize(22);
        title.setTextColor(Color.rgb(20, 60, 120));
        root.addView(title);

        prompt = new TextView(this);
        boolean vietnamese = LanguageSettings.isVietnamese(this);
        title.setText(vietnamese ? "Ôn tập lớp " + grade : "Practice grade " + grade);
        prompt.setText(vietnamese ? question.questionVi : question.questionEn);
        prompt.setTextSize(18);
        prompt.setPadding(0, 40, 0, 20);
        root.addView(prompt);

        choices = new RadioGroup(this);
        for (int index = 0; index < question.options.length; index++) {
            RadioButton choice = new RadioButton(this);
            choice.setText(question.getOption(index, vietnamese));
            choice.setTextSize(16);
            choices.addView(choice);
        }
        root.addView(choices);

        submit = new Button(this);
        submit.setText(vietnamese ? "Nộp bài" : "Submit");
        submit.setOnClickListener(view -> submitAnswer());
        root.addView(submit);
        setContentView(root);
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

        try (QuizDbHelper database = new QuizDbHelper(this)) {
            database.saveAttempt(grade, score, 1);
            if (!correct) {
                database.saveWrongQuestion(question.id, grade, question.questionVi,
                        question.questionEn, question.getCorrectAnswer(true),
                        question.getCorrectAnswer(false), question.getExplanation(true),
                        question.getExplanation(false));
            }
        }
        Toast.makeText(this, (LanguageSettings.isVietnamese(this)
                ? "Đã lưu kết quả: " : "Result saved: ") + score + "/1", Toast.LENGTH_LONG).show();
        finish();
    }
}





