package com.example.educare;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class StudentHomeworksActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //User variables
    String org;
    String UserName;
    String ClassID;

    //General data variables ***********************************************************************

    //Views
    TextView Name;
    RecyclerView homeworksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homeworks);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        UserName = UserData.getString("UserName", "not found");

        //getting data from Intent
        Intent intent = getIntent();
        ClassID = intent.getStringExtra("ClassId");
        //getting general vars *********************************************************************

        //Find views
        Name = findViewById(R.id.TVStudentHomeworksName);
        homeworksList = findViewById(R.id.RVHomeworkList);

        //Sets views
        Name.setText(UserName);

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

    private class Homework {
        Timestamp TimeDate;
        String date;
        String text;

        public Homework(Timestamp timeDate, String text) {
            TimeDate = timeDate;
            Date date = timeDate.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            this.date = sdf.format(date);
            this.text = text;
        }

        public Timestamp getTimeDate() {
            return TimeDate;
        }

        public void setTimeDate(Timestamp timeDate) {
            TimeDate = timeDate;
            Date date = timeDate.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            this.date = sdf.format(date);
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
    public class HomeworksAdapter extends RecyclerView.Adapter<HomeworksAdapter.HomeworksViewHolder>{
        ArrayList<Homework> homeworks;

        public HomeworksAdapter(ArrayList<Homework> homeworks) {
            this.homeworks = homeworks;
        }

        @NonNull
        @Override
        public HomeworksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View HomeworkView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_homework,parent,false);
            return new HomeworksViewHolder(HomeworkView);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeworksViewHolder holder, int position) {
            Homework currentHomework = homeworks.get(position);

            holder.text.setText(currentHomework.text);
            holder.date.setText(currentHomework.date);
        }

        @Override
        public int getItemCount() {
            return homeworks.size();
        }


        public static class HomeworksViewHolder extends RecyclerView.ViewHolder{
            TextView text;
            TextView date;

            public HomeworksViewHolder(@NonNull View itemView) {
                super(itemView);

                date = itemView.findViewById(R.id.RITVHomeworkDate);
                text = itemView.findViewById(R.id.RITVHomeworkText);
            }
        }
    }
}