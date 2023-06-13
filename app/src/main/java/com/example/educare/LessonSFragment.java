package com.example.educare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LessonSFragment extends Fragment {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Firestore variables
    SharedPreferences UserData;

    //User variables
    String org;
    String ClassID;

    //General data variables ***********************************************************************

    //Views
    TextView TVSubject;
    Button tests;
    Button Homeworks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_s, container, false);

        //getting general vars *********************************************************************
        //getting data from bundle
        Bundle args = getArguments();
        ClassID = args.getString("ClassroomId");

        //getting data from SharedPreferences
        UserData = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        //getting general vars *********************************************************************

        //Find views
        TVSubject = view.findViewById(R.id.TVFragmentSubject);
        tests = view.findViewById(R.id.BTNFragmentTests);
        Homeworks = view.findViewById(R.id.BTNFragmentHomework);

        //Sets views
        db.collection("organizations").document(org).collection("Classes")
                .document(ClassID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TVSubject.setText((String) documentSnapshot.get("Subject"));
                    }
                });

        tests.setOnClickListener(new View.OnClickListener() {
            //Move to StudentTestsActivity
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getActivity().getApplicationContext(), StudentTestsActivity.class);
                i.putExtra("ClassId",ClassID);
                startActivity(i);
            }
        });

        Homeworks.setOnClickListener(new View.OnClickListener() {
            //Move to StudentHomeworksActivity
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getActivity().getApplicationContext(), StudentHomeworksActivity.class);
                i.putExtra("ClassId",ClassID);
                startActivity(i);
            }
        });


        return view;
    }
}