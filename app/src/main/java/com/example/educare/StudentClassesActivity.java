package com.example.educare;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import java.util.ArrayList;

public class StudentClassesActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //Activity variables
    ArrayList<String> classes;

    //User variables
    String org;
    String UserName;

    //General data variables ***********************************************************************

    //Views
    RecyclerView classesList;
    Button unShow;
    Button noHomework;

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classes);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        UserName = UserData.getString("UserName", "not found");


        //getting general vars *********************************************************************

        //Find views
        classesList = findViewById(R.id.RVStudentClassesList);
        unShow = findViewById(R.id.BtnDisturbanceUnShows);
        noHomework = findViewById(R.id.BtnDisturbanceNoHomeworks);

        //Sets views

        unShow.setOnClickListener(disturbanceOnClick);
        noHomework.setOnClickListener(disturbanceOnClick);

        db.collection("organizations").document(org).collection("Student")
                .document(UserName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> dbClassesList = (ArrayList<String>) documentSnapshot.get("Classes");
                        if (dbClassesList == null) dbClassesList = new ArrayList<>();
                        classes = dbClassesList;
                        updateClassesList();
                    }
                });
    }

    View.OnClickListener disturbanceOnClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String disturbance = v==unShow ? "unShow" : "noHomeWork";

            Intent i =new Intent(getApplicationContext(), ShowDisturbanceActivity.class);
            i.putExtra("name", UserName);
            i.putExtra("disturbance",disturbance);
            startActivity(i);
        }
    };

    private void updateClassesList() {
        RecyclerView.LayoutManager classesListLayout = new LinearLayoutManager(StudentClassesActivity.this);
        classesAdapter classesListAdapter = new classesAdapter();
        classesList.setLayoutManager(classesListLayout);
        classesList.setAdapter(classesListAdapter);
    }

    private  class classesAdapter extends RecyclerView.Adapter<classesAdapter.classesViewHolder>{
        @NonNull
        @Override
        public classesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View classView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_studentclasses,parent,false);
            return new classesAdapter.classesViewHolder(classView);
        }

        @Override
        public void onBindViewHolder(@NonNull classesViewHolder holder, int position) {
            String currentClassID = classes.get(position);

            db.collection("organizations").document(org).collection("Classes")
                            .document(currentClassID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.className.setText((String) documentSnapshot.get("Subject"));
                        }
                    });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ClassroomId", currentClassID);
                    LessonSFragment myFragment = new LessonSFragment();
                    myFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.FContainerStudentClasses, myFragment);
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return classes.size();
        }

        public static class classesViewHolder extends RecyclerView.ViewHolder{
            TextView className;

            public classesViewHolder(@NonNull View itemView) {
                super(itemView);

                className = itemView.findViewById(R.id.RITVStudentClassName);
            }
        }
    }
}