package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClassesTActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //User variables
    String org;
    String UserName;

    //General data variables ***********************************************************************

    //Views
    RecyclerView classesList;
    Button addClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_tactivity);

        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        UserName = UserData.getString("UserName", "not found");
        //getting general vars *********************************************************************

        //Find views
        addClass =  findViewById(R.id.BTNAddClass);
        classesList = findViewById(R.id.RVClassesList);

        //Sets views
        addClass.setOnClickListener(new View.OnClickListener() {
            //Open the AddClassFragment
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("org", org);
                bundle.putString("user name", UserName);
                AddClassFragment myFragment = new AddClassFragment();
                myFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.FContainerClassesT, myFragment);
                fragmentTransaction.commit();
            }
        });

        updateClassesList();
    }

    private void updateClassesList() {

        db.collection("organizations").document(org).collection("Teacher")
                .document(UserName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Object existingClassesArray = documentSnapshot.get("Classes");
                        if (existingClassesArray instanceof List) {
                            ArrayList<String> classes = (ArrayList<String>) documentSnapshot.get("Classes");

                            RecyclerView.LayoutManager classesListLayout = new LinearLayoutManager(ClassesTActivity.this);
                            ClassAdapter classesListAdapter = new ClassAdapter(classes, org, ClassesTActivity.this);
                            classesList.setLayoutManager(classesListLayout);
                            classesList.setAdapter(classesListAdapter);
                        }
                    }
                });
    }

    private class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
        ArrayList<String> classes;
        String org;

        private Context context;

        //Only for accessing the Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Only for activating Fragment in Homework, Test Button is pressed

        public ClassAdapter(ArrayList<String> classes, String org, Context context) {
            this.classes = classes;
            this.org = org;
            this.context = context;
        }

        @NonNull
        @Override
        public ClassAdapter.ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View classView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_class_teacher,parent,false);
            return new ClassAdapter.ClassViewHolder(classView);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassAdapter.ClassViewHolder holder, int position) {
            String currentClass = classes.get(position);

            //General views
            db.collection("organizations").document(org).collection("Classes")
                    .document(currentClass).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.Subject.setText((String)documentSnapshot.get("Subject"));
                        }
                    });

            holder.Homework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("organizations").document(org).collection("Classes")
                            .document(currentClass).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Subject", (String)documentSnapshot.get("Subject"));
                                    bundle.putString("ClassID", currentClass);
                                    bundle.putString("org", org);

                                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                    AddHomeworkFragment fragment = new AddHomeworkFragment();
                                    fragment.setArguments(bundle);
                                    activity.getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.FContainerClassesT,fragment)
                                            .addToBackStack(null).commit();
                                }
                            });
                }
            });

            holder.Test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, AddTestActivity.class);
                    intent.putExtra("ClassroomId", currentClass);
                    context.startActivity(intent);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ClassDetailsActivity.class);
                    intent.putExtra("ClassroomId", currentClass);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return classes.size();
        }

        public static class ClassViewHolder extends RecyclerView.ViewHolder{

            Button Homework;
            Button Test;
            TextView Subject;

            public ClassViewHolder(@NonNull View itemView) {
                super(itemView);

                Homework = itemView.findViewById(R.id.RIBTNAddHomework);
                Test = itemView.findViewById(R.id.RIBTNAddTest);
                Subject = itemView.findViewById(R.id.RITVSubject);
            }
        }
    }
}