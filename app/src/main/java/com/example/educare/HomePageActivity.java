package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class HomePageActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //ProgressDialog variables
    ProgressDialog progressDialog;

    //Activity variables
    int day;
    Calendar calendar = Calendar.getInstance();
    AlarmManager alarmManager;

    ArrayList<String> Classes = new ArrayList<>();
    ArrayList<Lesson> lessons = new ArrayList<>();

    //User variables
    String org;
    String tORs;
    String UserName;

    //General data variables ***********************************************************************

    //Views
    TextView Name;
    RecyclerView timetable;
    Button SetNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //getting general vars *********************************************************************
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        tORs = UserData.getString("tORs", "not found");
        UserName = UserData.getString("UserName", "not found");
        day = calendar.get(Calendar.DAY_OF_WEEK);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading today classes");
        progressDialog.setCancelable(true);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //getting general vars *********************************************************************

        //Find views
        Name = findViewById(R.id.TVName);
        timetable = findViewById(R.id.RVTimetable);
        SetNotification = findViewById(R.id.BTNSetNotification);

        //Sets views

        Name.setText(UserName);

        SetNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        // Building lessons from Firestore
        db.collection("organizations").document(org).collection(tORs).document(UserName)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Classes = (ArrayList<String>) documentSnapshot.get("Classes");
                    updateTimeTable();
                }
            }
        });
    }

    private void setAlarm() {
        //TODO: change this to the needded time

        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the alarm time to 10 seconds from the current time
        calendar.add(Calendar.SECOND, 40);

        // Create an intent to launch the notification
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Check if the app has the SET_ALARM permission
        String permission = Manifest.permission.SET_ALARM;
        int granted = PackageManager.PERMISSION_GRANTED;
        if (checkSelfPermission(permission) == granted) {
            // Set the alarm using AlarmManager
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } catch (SecurityException e) {
                // Handle SecurityException if thrown
                e.printStackTrace();
            }
        } else {
            // Handle case when permission is not granted
            // Request the permission from the user or take appropriate action
            Toast.makeText(this, "SET_ALARM permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTimeTable() {
        if(Classes == null) {return;}
        CollectionReference colRef = db.collection("organizations")
                .document(org).collection("Classes");

        progressDialog.show();
        for (int i = 0; i <= Classes.size(); i++) {
            if (i < Classes.size()) {
                DocumentReference docRef = colRef.document(Classes.get(i));
                int finalI = i;
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshotOrig) {
                        ArrayList<Boolean> lessonDays = (ArrayList<Boolean>) documentSnapshotOrig.get("Days");
                        if (lessonDays == null)return;
                        if (lessonDays.get(day-1)) {
                            docRef.collection("Lessons").document(day + "")
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            ArrayList<String> lessonsID = (ArrayList<String>) documentSnapshot.get("lessonsID");
                                            if (lessonsID == null) return;
                                            for (int j = 0; j < lessonsID.size(); j++) {
                                                db.collection("organizations").document(org)
                                                        .collection("Lessons").document(lessonsID.get(j))
                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot doc) {
                                                                Integer startHour = doc.getLong("startHour").intValue();
                                                                Integer startMinute = doc.getLong("startMinute").intValue();
                                                                Integer endHour = doc.getLong("endHour").intValue();
                                                                Integer endMinute = doc.getLong("endMinute").intValue();
                                                                String subject = documentSnapshotOrig.getString("Subject");
                                                                lessons.add(new Lesson(subject, new Time(startHour, startMinute), new Time(endHour, endMinute),Classes.get(finalI)));

                                                                RecyclerView.LayoutManager timetableLayout = new LinearLayoutManager(HomePageActivity.this);
                                                                LessonAdapter timetableAdapter = new LessonAdapter(lessons,tORs,HomePageActivity.this);
                                                                timetable.setLayoutManager(timetableLayout);
                                                                timetable.setAdapter(timetableAdapter);
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        }

    }

    private class Lesson {
        String subject;
        Time Start;
        Time End;
        String ClassroomID;

        public Lesson(String className, Time start, Time end, String classroomID) {
            this.subject = className;
            Start = start;
            End = end;
            ClassroomID = classroomID;
        }

        public String getClassroomID() {
            return ClassroomID;
        }

        public void setClassroomID(String classroomID) {
            ClassroomID = classroomID;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Time getStart() {
            return Start;
        }

        public void setStart(Time start) {
            Start = start;
        }

        public Time getEnd() {
            return End;
        }

        public void setEnd(Time end) {
            End = end;
        }
    }
    public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder>{
        ArrayList<Lesson> lessons;
        String tORs;


        public LessonAdapter(ArrayList<Lesson> lessons, String tORs, FragmentActivity fragmentActivity) {
            this.lessons = lessons;
            this.tORs = tORs;
        }

        @NonNull
        @Override
        public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View lessonView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_lesson,parent,false);
            return new LessonViewHolder(lessonView);
        }

        @Override
        public int getItemCount() {
            return lessons.size();
        }

        @Override
        public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
            Lesson currentLesson = lessons.get(position);

            holder.className.setText(currentLesson.getSubject());
            holder.Time.setText(currentLesson.getStart().toString() +"-"+ currentLesson.getEnd().toString());

            // Set OnClickListener on the view
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start new activity with the ClassroomID as extra
                    if (tORs.equals("Teacher")){
                        Intent i = new Intent(view.getContext(), LessonTActivity.class);
                        i.putExtra("ClassroomId", lessons.get(holder.getAdapterPosition()).getClassroomID());
                        i.putExtra("Subject", lessons.get(holder.getAdapterPosition()).getSubject());
                        i.putExtra("StartHour", lessons.get(holder.getAdapterPosition()).getStart().Hour);
                        i.putExtra("StartMinutes", lessons.get(holder.getAdapterPosition()).getStart().Minutes);
                        i.putExtra("EndHour", lessons.get(holder.getAdapterPosition()).getEnd().Hour);
                        i.putExtra("EndMinutes", lessons.get(holder.getAdapterPosition()).getEnd().Minutes);
                        view.getContext().startActivity(i);
                    }
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString("ClassroomId", lessons.get(holder.getAdapterPosition()).getClassroomID());
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        LessonSFragment fragment = new LessonSFragment();
                        fragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.FContainerLessonS,fragment)
                                .addToBackStack(null).commit();
                    }

                }
            });
        }

        public static class LessonViewHolder extends RecyclerView.ViewHolder{

            public TextView className;
            public TextView Time;

            public LessonViewHolder(@NonNull View itemView) {
                super(itemView);

                className = itemView.findViewById(R.id.RITVClassName);
                Time = itemView.findViewById(R.id.RITVTime);
            }
        }
    }

}