package com.example.educare;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowDisturbanceActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //User variables
    String org;
    String disturbance;
    String name;
    String classID;

    //General data variables ***********************************************************************

    //Views
    TextView TVName;
    TextView TVDisturbance;
    RecyclerView disturbanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_disturbance);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");

        //getting data from Intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        disturbance = intent.getStringExtra("disturbance");
        classID = intent.getStringExtra("ClassId");

        //getting general vars *********************************************************************

        //Find views
        TVName = findViewById(R.id.TVStudentDisturbanceName);
        TVDisturbance = findViewById(R.id.TVDisturbance);
        disturbanceList = findViewById(R.id.RVDisturbanceList);

        //Sets views
        TVName.setText(name);
        TVDisturbance.setText(disturbance);

        updateDisturbanceList();
    }

    private void updateDisturbanceList() {
        db.collection("organizations").document(org).collection("Student")
                .document(name).collection("disturbance").document(disturbance)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> subjects = (ArrayList<String>) documentSnapshot.get("Class");
                        if (subjects == null) subjects = new ArrayList<>();
                        ArrayList<Timestamp> dates = (ArrayList<Timestamp>) documentSnapshot.get("date");
                        if (dates == null) dates = new ArrayList<>();

                        RecyclerView.LayoutManager disturbanceListLayout = new LinearLayoutManager(ShowDisturbanceActivity.this);
                        DisturbanceListAdapter disturbanceListAdapter = new DisturbanceListAdapter(subjects,dates,classID);
                        disturbanceList.setLayoutManager(disturbanceListLayout);
                        disturbanceList.setAdapter(disturbanceListAdapter);
                    }
                });

    }

    private class DisturbanceListAdapter extends RecyclerView.Adapter<DisturbanceListAdapter.DisturbanceListViewHolder>{
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        public DisturbanceListAdapter(ArrayList<String> subjectsIDs, ArrayList<Timestamp> Dates, String requiredClass) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 0; i < Dates.size(); i++) {
                if((requiredClass == null) || (requiredClass.equals(subjectsIDs.get(i)))) {
                    subjects.add(subjectsIDs.get(i));
                    Date date = Dates.get(i).toDate();
                    dates.add(sdf.format(date));
                }
            }
        }

        @NonNull
        @Override
        public DisturbanceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View disturbanceView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_disturbance,parent,false);
            return new DisturbanceListViewHolder(disturbanceView);
        }

        @Override
        public void onBindViewHolder(@NonNull DisturbanceListViewHolder holder, int position) {
            db.collection("organizations").document(org).collection("Classes")
                            .document(subjects.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.subject.setText((String) documentSnapshot.get("Subject"));
                        }
                    });
            holder.date.setText(dates.get(position));
        }

        @Override
        public int getItemCount() {
            return subjects.size();
        }

        public static class DisturbanceListViewHolder extends RecyclerView.ViewHolder{
            TextView subject;
            TextView date;

            public DisturbanceListViewHolder(@NonNull View itemView) {
                super(itemView);

                subject = itemView.findViewById(R.id.RITVDisturbanceSubject);
                date = itemView.findViewById(R.id.RITVDisturbanceDate);
            }
        }
    }
}