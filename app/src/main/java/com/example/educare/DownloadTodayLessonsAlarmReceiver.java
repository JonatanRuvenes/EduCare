package com.example.educare;

import android.app.job.JobParameters;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class DownloadTodayLessonsAlarmReceiver extends BroadcastReceiver {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //Service variables
    int day;
    Calendar calendar = Calendar.getInstance();

    //User variables
    String org;
    String tORs;
    String UserName;

    //General data variables ***********************************************************************


    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getBooleanExtra("isConnect",false)){
            return;
        }
        intent.getStringExtra("org");
        //getting general vars *********************************************************************
        org = intent.getStringExtra("org");
        tORs = intent.getStringExtra("tORs");
        UserName = intent.getStringExtra("UserName");
        day = calendar.get(Calendar.DAY_OF_WEEK);
        //getting general vars *********************************************************************

        doBackgroundWork();
    }

    private void doBackgroundWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("organizations").document(org).collection(tORs).document(UserName)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    ArrayList<String> Classes = (ArrayList<String>) documentSnapshot.get("Classes");
                                    if(Classes == null) {return;}
                                    ArrayList<Lesson> lessons = new ArrayList<>();
                                    CollectionReference colRef = db.collection("organizations")
                                            .document(org).collection("Classes");

                                    final Boolean[] uploading = {false};
                                    for (int i = 0; i <= Classes.size(); i++) {
                                        while (uploading[0]);
                                        uploading[0] = true;
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
                                                                                            HomePageActivity.lessons = lessons;
                                                                                            uploading[0] = false;
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
                        });
            }
        }).start();
    }

}
