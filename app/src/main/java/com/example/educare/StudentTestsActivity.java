package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StudentTestsActivity extends AppCompatActivity {

    SharedPreferences UserData;
    String org;
    String UserName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String ClassID;

    //Views
    TextView Name;
    RecyclerView testsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tests);

        UserData = getSharedPreferences("UserData", MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        UserName = UserData.getString("UserName", "not found");

        //importing classes list
        Intent intent = getIntent();
        ClassID = intent.getStringExtra("ClassId");

        ClassID = "none";

        Name = findViewById(R.id.TVStudentName);
        Name.setText(UserName);

        testsList = findViewById(R.id.RVTestsList);

        updateTestsList();
    }

    private void updateTestsList() {
        ArrayList<TestOutput> tests = new ArrayList<>();
        //ToDo: get data from firebase

        RecyclerView.LayoutManager testsListLayout = new LinearLayoutManager(StudentTestsActivity.this);
        TestAdapter testsListAdapter = new TestAdapter(tests);

        testsList.setLayoutManager(testsListLayout);
        testsList.setAdapter(testsListAdapter);
    }
}