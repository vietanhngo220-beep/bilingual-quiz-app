package com.vietanh.bilingualquiz.data;

import android.content.Context;
import com.vietanh.bilingualquiz.model.Question;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
    private final Context context;
    public QuestionRepository(Context context) { this.context = context.getApplicationContext(); }
    public List<Question> getSampleQuestions(int grade) {
        List<Question> list = new ArrayList<>();
        Question q = new Question(); q.id=1; q.grade=grade; q.subject="General";
        q.questionVi="Mẫu câu hỏi cho lớp "+grade+" là gì?"; q.questionEn="What is this sample question for grade "+grade+"?";
        q.options=new String[]{"Đáp án A / Answer A","Đáp án B / Answer B","Đáp án C / Answer C","Đáp án D / Answer D"}; q.correctIndex=0;
        q.explanation="Dữ liệu mẫu để nhóm thay bằng 300 câu hỏi JSON."; q.explanationVi=q.explanation; q.explanationEn="Sample data for the team to replace with 300 JSON questions."; list.add(q); return list;
    }
}
