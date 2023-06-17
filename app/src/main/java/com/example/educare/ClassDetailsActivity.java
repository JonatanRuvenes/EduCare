package com.example.educare;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class ClassDetailsActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //User variables
    String org;
    String classID;

    //General data variables ***********************************************************************

    //Views
    RecyclerView lists;
    TextView listKind;
    TextView Subject;
    Button student;
    Button lessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");

        //getting data from Intent
        Intent intent = getIntent();
        classID = intent.getStringExtra("ClassroomId");

        //getting general vars *********************************************************************

        //Find views
        listKind = findViewById(R.id.TVListKId);
        Subject = findViewById(R.id.TVClassSubject);
        lists = findViewById(R.id.RVStudentsInClass);
        student = findViewById(R.id.BTNStudentsList);
        lessons = findViewById(R.id.BTNLessonsList);

        //Sets views
        db.collection("organizations").document(org).collection("Classes")
                .document(classID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot){
                        Subject.setText((String) documentSnapshot.get("Subject"));
                    }
                });

        RecyclerView.LayoutManager listLayout = new LinearLayoutManager(ClassDetailsActivity.this);
        lists.setLayoutManager(listLayout);

        student.setOnClickListener(new View.OnClickListener() {
            //changing the lists RecyclerView to present StudentsList
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

        lessons.setOnClickListener(new View.OnClickListener() {
            //changing the lists RecyclerView to present LessonsList
            @Override
            public void onClick(View v) {
                listKind.setText("Lessons");

                updateLessonsList();

            }
        });
    }

    public void updateLessonsList(){
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
                                    ArrayList<String> finalLessons = lessons;
                                    int finalI = i;
                                    db.collection("organizations").document(org).collection("Lessons")
                                            .document(lessons.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot document) {
                                                    Time start = new Time(Math.toIntExact((long) document.get("startHour")),Math.toIntExact((long) document.get("startMinute")));
                                                    Time end = new Time(Math.toIntExact((long) document.get("endHour")),Math.toIntExact((long) document.get("endMinute")));

                                                    String time = start.toString() +"-"+ end.toString();
                                                    Lessons.add(new Lesson(day, time, finalLessons.get(finalI)));
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
        int intDay;
        String day;
        String time;
        String lessonID;

        public Lesson(int  day, String time, String lessonID) {
            this.day = daysOfWeek[day-1];
            this.intDay = day;
            this.time = time;
            this.lessonID = lessonID;
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

        public String getLessonID() {
            return lessonID;
        }

        public void setLessonID(String lessonID) {
            this.lessonID = lessonID;
        }

        public int getIntDay() {
            return intDay;
        }

        public void setIntDay(int intDay) {
            this.intDay = intDay;
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
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = new Dialog(ClassDetailsActivity.this);
                    dialog.setContentView(R.layout.dialog_deletelesson);

                    // Find the buttons in the dialog layout
                    Button yes = dialog.findViewById(R.id.DialogBTNYes);
                    Button no = dialog.findViewById(R.id.DialogBTNNo);

                    // Set click listeners for the buttons inside the dialog
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.collection("organizations").document(org).collection("Lessons").document(currentLesson.getLessonID())
                                            .delete();
                            DocumentReference docRef = db.collection("organizations").document(org).collection("Classes").document(classID)
                                    .collection("Lessons").document(currentLesson.getIntDay()+"");
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            ArrayList<String> lessonsIDs = (ArrayList<String>) documentSnapshot.get("lessonsID");
                                            if(lessonsIDs == null) lessonsIDs = new ArrayList<>();

                                            if (lessonsIDs.size() <= 1)
                                                docRef.delete();
                                            else {
                                                lessonsIDs.remove(currentLesson.getLessonID());
                                                docRef.update("lessonsID", lessonsIDs);
                                            }

                                            updateLessonsList();
                                        }
                                    });
                            dialog.dismiss();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    return false;
                }
            });
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