package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClassesTActivity extends AddMenuActivity {

    // User info
    SharedPreferences UserData;
    String org;
    String UserName;

    // Importing data from Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //views
    RecyclerView classesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_tactivity);

        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        UserName = UserData.getString("UserName", "not found");

        classesList = findViewById(R.id.RVClassesList);
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
}