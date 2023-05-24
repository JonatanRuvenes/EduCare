package com.example.educare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    ArrayList<TestOutput> tests;

    public TestAdapter(ArrayList<TestOutput> tests) {
        this.tests = tests;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View testView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_test, parent, false);
        return new TestViewHolder(testView);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        TestOutput currentTest = tests.get(position);

        holder.testName.setText(currentTest.getTestName());
        holder.Grade.setText(currentTest.getGrade()+"");
        holder.subject.setText(currentTest.getSubject());
        holder.date.setText(currentTest.getDate());
        holder.teachersName.setText(currentTest.getTestName());
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder{

        public TextView testName;
        public TextView Grade;
        public TextView subject;
        public TextView date;
        public TextView teachersName;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);

            testName = itemView.findViewById(R.id.RITVTestName);
            Grade = itemView.findViewById(R.id.RITVTestGrade);
            subject = itemView.findViewById(R.id.RITVTestSubject);
            date = itemView.findViewById(R.id.RITVTestDate);
            teachersName = itemView.findViewById(R.id.RITVTestTeacher);
        }
    }
}
