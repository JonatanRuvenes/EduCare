package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentHomeworksActivity extends AddMenuActivity {

    SharedPreferences UserData;
    String org;
    String UserName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String ClassID;

    TextView Name;
    RecyclerView homeworksList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homeworks);

        UserData = getSharedPreferences("UserData", MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        UserName = UserData.getString("UserName", "not found");

        //importing classes list
        Intent intent = getIntent();
        ClassID = intent.getStringExtra("ClassId");

        Name = findViewById(R.id.TVStudentHomeworksName);
        Name.setText(UserName);

        homeworksList = findViewById(R.id.RVHomeworkList);

        updateHomeworksList();
    }

    private void updateHomeworksList() {
        ArrayList<Homework> homeworks = new ArrayList<>();
        db.collection("organizations").document(org).collection("Student")
                .document(UserName).collection("Homeworks").document(ClassID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<Timestamp> date = (ArrayList<Timestamp>) documentSnapshot.get("date");
                        if (date == null) date = new ArrayList<>();
                        ArrayList<String> text = (ArrayList<String>) documentSnapshot.get("text");
                        if (text == null) text = new ArrayList<>();

                        for(int i = 0; i<date.size(); i++){
                            homeworks.add(new Homework(date.get(i),text.get(i)));
                        }

                        RecyclerView.LayoutManager homeworksListLayout = new LinearLayoutManager(StudentHomeworksActivity.this);
                        HomeworksAdapter homeworksListAdapter = new HomeworksAdapter(homeworks);

                        homeworksList.setLayoutManager(homeworksListLayout);
                        homeworksList.setAdapter(homeworksListAdapter);
                    }
                });
    }
}