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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    Button lessons;
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

        lessons = findViewById(R.id.BTNLessonsList);
        lessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listKind.setText("Lessons");

                db.collection("organizations").document(org).collection("Classes")
                        .document(classID).collection("Lessons").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<Lesson> Lessons= new ArrayList<>();
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        int day = Integer.parseInt(document.getId());
                                        ArrayList<String> lessons = (ArrayList<String>) document.get("lessonsID");
                                        if (lessons == null) lessons = new ArrayList<>();
                                        for (int i = 0; i < lessons.size(); i++) {
                                            db.collection("organizations").document(org).collection("Lessons")
                                                    .document(lessons.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot document) {
                                                            Time start = new Time(Math.toIntExact((long) document.get("startHour")),Math.toIntExact((long) document.get("startMinute")));
                                                            Time end = new Time(Math.toIntExact((long) document.get("endHour")),Math.toIntExact((long) document.get("endMinute")));

                                                            String time = start.toString() +"-"+ end.toString();
                                                            Lessons.add(new Lesson(day, time));
                                                            LessonsListAdapter listAdapter = new LessonsListAdapter(Lessons);
                                                            lists.setAdapter(listAdapter);
                                                        }
                                                    });
                                        }
                                    }

                                }
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
    private class Lesson{
        static String[] daysOfWeek = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        };
        String day;
        String time;

        public Lesson(int  day, String time) {
            this.day = daysOfWeek[day];
            this.time = time;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
    private class LessonsListAdapter extends RecyclerView.Adapter<LessonsListAdapter.LessonsListViewHolder>{
        ArrayList<Lesson> lessons;

        public LessonsListAdapter(ArrayList<Lesson> lessons) {
            this.lessons = lessons;
        }

        @NonNull
        @Override
        public LessonsListAdapter.LessonsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View lessonView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_lessoninclass,parent,false);
            return new LessonsListAdapter.LessonsListViewHolder(lessonView);
        }

        @Override
        public void onBindViewHolder(@NonNull LessonsListAdapter.LessonsListViewHolder holder, int position) {
            Lesson currentLesson = lessons.get(position);

            holder.time.setText(currentLesson.getTime());
            holder.day.setText(currentLesson.getDay());
        }

        @Override
        public int getItemCount() {
            return lessons.size();
        }

        public static class LessonsListViewHolder extends RecyclerView.ViewHolder{
            TextView day;
            TextView time;
            public LessonsListViewHolder(@NonNull View itemView) {
                super(itemView);

                day = itemView.findViewById(R.id.RITVDay);
                time = itemView.findViewById(R.id.RITVLessonTime);
            }
        }
    }

}