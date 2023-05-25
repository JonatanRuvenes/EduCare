package com.example.educare;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        Name = findViewById(R.id.TVStudentName);
        Name.setText(UserName);

        testsList = findViewById(R.id.RVTestsList);

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
}