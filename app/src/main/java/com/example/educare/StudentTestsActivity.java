package com.example.educare;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StudentTestsActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //User variables
    String org;
    String UserName;
    String ClassID;

    //General data variables ***********************************************************************

    //Views
    TextView Name;
    RecyclerView testsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tests);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", MODE_PRIVATE);
        org = UserData.getString("org", "not found");

        //getting data from Intent
        Intent intent = getIntent();
        UserName = intent.getStringExtra("UserName");
        ClassID = intent.getStringExtra("ClassId");
        //getting general vars *********************************************************************

        //Find views
        Name = findViewById(R.id.TVStudentTestName);
        testsList = findViewById(R.id.RVTestsList);

        //Sets views

        Name.setText(UserName);

        updateTestsList();
    }

    private void updateTestsList() {
        ArrayList<TestOutput> tests = new ArrayList<>();
        db.collection("organizations").document(org).collection("Student")
                .document(UserName).collection("Tests").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.get("classID").toString().equals(ClassID)){
                                Timestamp timestamp = documentSnapshot.getTimestamp("date");
                                Date date = timestamp.toDate();

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = sdf.format(date);

                                String TestName = documentSnapshot.getString("testName");
                                int grade = documentSnapshot.getLong("grade").intValue();
                                String subject = documentSnapshot.getString("subject");
                                String teachersName = documentSnapshot.getString("teachers_name");

                                tests.add(new TestOutput(TestName, grade, subject, teachersName, formattedDate));
                            }
                        }
                        RecyclerView.LayoutManager testsListLayout = new LinearLayoutManager(StudentTestsActivity.this);
                        TestAdapter testsListAdapter = new TestAdapter(tests);

                        testsList.setLayoutManager(testsListLayout);
                        testsList.setAdapter(testsListAdapter);
                    }
                });
    }

    public class TestOutput extends Test {
        String date;

        public TestOutput(String testName, int grade, String subject, String teachers_name, String date) {
            super(testName, grade, subject, teachers_name);
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
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
}