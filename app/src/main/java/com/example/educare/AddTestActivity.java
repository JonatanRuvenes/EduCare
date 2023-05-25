package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class AddTestActivity extends AddMenuActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SharedPreferences UserData;

    StudentTestListAdapter studentsTestListAdapter;


    ArrayList<String> students = new ArrayList<>();
    String ClassroomID;
    String teachers_name;

    String org;

    TextView Subject;
    RecyclerView studentList;
    Button add;
    EditText testName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");

        Intent i = getIntent();
        ClassroomID = i.getStringExtra("ClassroomId");

        Subject = findViewById(R.id.TVAddTestSubject);
        db.collection("organizations").document(org).collection("Classes")
                .document(ClassroomID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Subject.setText(documentSnapshot.get("Subject").toString());
                        teachers_name = documentSnapshot.get("teacher").toString();
                    }
                });


        studentList =findViewById(R.id.RVTestStudentsList);
        createStudentsList();

        testName = findViewById(R.id.ETTestName);
        add = findViewById(R.id.BTNAddTest);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testName.getText().toString().equals("")){
                    Toast.makeText(AddTestActivity.this, "not all the fields are full", Toast.LENGTH_SHORT).show();
                    return;
                }
                CollectionReference colRef = db.collection("organizations").document(org).collection("Student");
                for (int i = 0; i < students.size();i++){
                    DocumentReference docRef = colRef.document(students.get(i)).collection("Tests").document(testName.getText().toString());

                    String Name = testName.getText().toString();
                    String subject = Subject.getText().toString();
                    int grade = studentsTestListAdapter.grades[i];
                    long currentTimeMillis = System.currentTimeMillis();
                    long currentTimeWithoutTime = (currentTimeMillis / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000);
                    Timestamp date = new Timestamp(currentTimeWithoutTime / 1000, 0);
                    TestInput test = new TestInput(Name, grade, subject, teachers_name, ClassroomID, date);
                    docRef.set(test);
                }
                finish();
            }
        });
    }

    private void createStudentsList() {
        //finding data from firebase
        DocumentReference docRef = db.collection("organizations").document(org)
                .collection("Classes")
                .document(ClassroomID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                students = (ArrayList<String>) documentSnapshot.get("Students");

                RecyclerView.LayoutManager studentsTestListLayout = new LinearLayoutManager(AddTestActivity.this);
                studentsTestListAdapter = new StudentTestListAdapter(students, org);
                studentList.setLayoutManager(studentsTestListLayout);
                studentList.setAdapter(studentsTestListAdapter);
            }
        });
    }
}