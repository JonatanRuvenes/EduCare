package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;import android.content.SharedPreferences;
import android.os.Bundle;
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

    // User info
    SharedPreferences UserData;
    String org;
    String tORs;
    String UserName;
    int day;

    // Importing data from Firestore
    Calendar calendar = Calendar.getInstance();
    SharedPreferences lessonSP;
    SharedPreferences.Editor lessonSPEditor;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> Classes = new ArrayList<>();

    // Views
    TextView Name;
    RecyclerView timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        tORs = UserData.getString("tORs", "not found");
        UserName = UserData.getString("UserName", "not found");
        day = calendar.get(Calendar.DAY_OF_WEEK);

        //ToDo: Delete This Line
        day = 2;

        // General vars
        Name = findViewById(R.id.TVName);
        Name.setText(UserName);

        // Building lessons from Firestore
        timetable = findViewById(R.id.RVTimetable);
        DocumentReference docRef = db.collection("organizations")
                .document(org).collection(tORs).document(UserName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Classes = (ArrayList<String>) documentSnapshot.get("Classes");
                    updateTimeTable();
                }
            }
        });
    }

    //TODO: fix the runtime of this method
    public void updateTimeTable() {
        if(Classes == null) {return;}
        ArrayList<Lesson> lessons = new ArrayList<>();
        CollectionReference colRef = db.collection("organizations")
                .document(org).collection("Classes");
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

}