package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.security.auth.Subject;

public class ClassDetailsActivity extends AddMenuActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences UserData;
    String org;
    String classID;
    RecyclerView lists;
    TextView listKind;
    TextView Subject;
    Button student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        Intent intent = getIntent();
        classID = intent.getStringExtra("ClassroomId");

        listKind = findViewById(R.id.TVListKId);
        Subject = findViewById(R.id.TVClassSubject);
        db.collection("organizations").document(org).collection("Classes")
                .document(classID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot){
                        Subject.setText((String) documentSnapshot.get("Subject"));
                    }
                });

        lists = findViewById(R.id.RVStudentsInClass);
        RecyclerView.LayoutManager listLayout = new LinearLayoutManager(ClassDetailsActivity.this);
        lists.setLayoutManager(listLayout);


        student = findViewById(R.id.BTNStudentsList);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listKind.setText("Students");

                db.collection("organizations").document(org).collection("Classes")
                        .document(classID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ArrayList<String> students = (ArrayList<String>) documentSnapshot.get("Students");
                                if (students == null) students = new ArrayList<>();

                                StudentsListInClassAdapter listAdapter = new StudentsListInClassAdapter(students);
                                lists.setAdapter(listAdapter);
                            }
                        });
            }
        });
    }

    private class StudentsListInClassAdapter extends RecyclerView.Adapter<StudentsListInClassAdapter.StudentsListInClassViewHolder>{
        ArrayList<String> students;

        public StudentsListInClassAdapter(ArrayList<String> students) {
            this.students = students;
        }

        @NonNull
        @Override
        public StudentsListInClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View StudentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_studentinclass,parent,false);
            return new StudentsListInClassViewHolder(StudentView);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentsListInClassViewHolder holder, int position) {
            String currentStudent = students.get(position);

            holder.name.setText(currentStudent);
            holder.unShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i =new Intent(getApplicationContext(), ShowDisturbanceActivity.class);
                    i.putExtra("name", currentStudent);
                    i.putExtra("disturbance","unShow");
                    i.putExtra("ClassId", classID);
                    startActivity(i);
                }
            });
            holder.noHomework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i =new Intent(getApplicationContext(), ShowDisturbanceActivity.class);
                    i.putExtra("name", currentStudent);
                    i.putExtra("disturbance","noHomeWork");
                    i.putExtra("ClassId", classID);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        public static class StudentsListInClassViewHolder extends RecyclerView.ViewHolder{
            TextView name;
            Button unShow;
            Button noHomework;

            public StudentsListInClassViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.RITVStudentInClassName);
                unShow = itemView.findViewById(R.id.RIBTNUnShow);
                noHomework = itemView.findViewById(R.id.RIBTNNoHomework);
            }
        }
    }
}