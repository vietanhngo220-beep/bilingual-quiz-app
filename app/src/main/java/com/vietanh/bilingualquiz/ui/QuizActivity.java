package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import com.vietanh.bilingualquiz.data.QuestionRepository;
import com.vietanh.bilingualquiz.model.Question;

public class QuizActivity extends Activity {
    int grade; int index=0; int score=0; Question question;
    LinearLayout root; TextView title, prompt; RadioGroup choices; Button next;
    @Override public void onCreate(Bundle b){super.onCreate(b); grade=getIntent().getIntExtra("grade",8); showQuestion();}
    void showQuestion(){ question=new QuestionRepository(this).getSampleQuestions(grade).get(0); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,32,32,32);
        title=new TextView(this); title.setText("Ôn tập lớp "+grade+" / Practice grade "+grade); title.setTextSize(22); title.setTextColor(Color.rgb(20,60,120)); root.addView(title);
        prompt=new TextView(this); prompt.setText(question.questionVi+"\n"+question.questionEn); prompt.setTextSize(18); prompt.setPadding(0,40,0,20); root.addView(prompt);
        choices=new RadioGroup(this); for(String option:question.options){RadioButton r=new RadioButton(this); r.setText(option); r.setTextSize(16); choices.addView(r);} root.addView(choices);
        next=new Button(this); next.setText("Nộp bài / Submit"); next.setOnClickListener(v->{if(choices.getCheckedRadioButtonId()!=-1) score=1; Toast.makeText(this,"Đã nộp. Điểm: "+score+"/1",Toast.LENGTH_LONG).show(); finish();}); root.addView(next); setContentView(root);}
}
