package com.example.educare;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class AddTestActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //Activity variables

    ArrayList<String> students = new ArrayList<>();
    StudentTestListAdapter studentsTestListAdapter;

    //User variables
    String org;
    String ClassroomID;
    String teachers_name;

    //General data variables ***********************************************************************

    //Views
    TextView Subject;
    RecyclerView studentList;
    Button add;
    EditText testName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");

        //getting data from Intent
        Intent i = getIntent();
        ClassroomID = i.getStringExtra("ClassroomId");

        //getting general vars *********************************************************************

        //Find views
        Subject = findViewById(R.id.TVAddTestSubject);
        studentList =findViewById(R.id.RVTestStudentsList);
        testName = findViewById(R.id.ETTestName);
        add = findViewById(R.id.BTNAddTest);

        //Sets views
        //find the Subject and the teachers_name in the firestore
        db.collection("organizations").document(org).collection("Classes")
                .document(ClassroomID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Subject.setText(documentSnapshot.get("Subject").toString());
                        teachers_name = documentSnapshot.get("teacher").toString();
                    }
                });


        createStudentsList();


        //adding the test grades to the firestore
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

    //creating the views in the studentList RecyclerView
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

    //class Used in this activity to add the Test to firestore
    private class TestInput extends Test{
        String ClassID;
        Timestamp date;

        public String getClassID() {
            return ClassID;
        }

        public void setClassID(String classID) {
            ClassID = classID;
        }

        public Timestamp getDate() {
            return date;
        }

        public void setDate(Timestamp date) {
            this.date = date;
        }

        public TestInput(String testName, int grade, String subject, String teachers_name, String classID, Timestamp date) {
            super(testName, grade, subject, teachers_name);
            ClassID = classID;
            this.date = date;
        }
    }

    private class StudentTestListAdapter extends RecyclerView.Adapter<StudentTestListAdapter.StudentTestListViewHolder>{

        ArrayList<String> students;
        String org;

        public static int[] grades;


        public StudentTestListAdapter(ArrayList<String> students, String org){
            this.students = students;
            this.org = org;
            this.grades = new int[students.size()];
        }

        @Override
        public StudentTestListAdapter.StudentTestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View studentTestView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_studentstest,parent,false);
            return new StudentTestListAdapter.StudentTestListViewHolder(studentTestView);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull StudentTestListAdapter.StudentTestListViewHolder holder, int position) {
            String currentStudent = students.get(position);

            int index = position;
            holder.name.setText(currentStudent);

            holder.grade.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    grades[index] = Integer.parseInt(s.toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        public static class StudentTestListViewHolder extends RecyclerView.ViewHolder{
            TextView name;
            EditText grade;

            public StudentTestListViewHolder(@NonNull View itemView){
                super(itemView);

                name = itemView.findViewById(R.id.RITVStudentNameForTest);
                grade = itemView.findViewById(R.id.RIETGrade);
            }
        }
    }
}