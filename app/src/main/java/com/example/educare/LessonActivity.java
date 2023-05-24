package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class LessonActivity extends AppCompatActivity {

    RecyclerView studentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        //ToDo: get the student list from the intent
        ArrayList<User> students = new ArrayList<>();
        for(int i=0; i<20;i++){
            students.add(
                    new User(i+"",i+"","Rony","Student"));
        }

        RecyclerView.LayoutManager studentsListLayout = new LinearLayoutManager(this);
        StudentsAdapter studentsListAdapter = new StudentsAdapter(students);

        studentsList = findViewById(R.id.RVStudentsList);
        studentsList.setLayoutManager(studentsListLayout);
        studentsList.setAdapter(studentsListAdapter);
    }
}