package com.example.educare;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

//This is RecyclerView Adapter
//Using in ClassesTActivity
public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
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
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View classView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_class_teacher,parent,false);
        return new ClassViewHolder(classView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String currentClass = classes.get(position);

        //General views
        final String[] subject = {""};
        db.collection("organizations").document(org).collection("Classes")
                        .document(currentClass).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        subject[0] = (String) documentSnapshot.get("Subject");
                        holder.Subject.setText(subject[0]);
                    }
                });

        holder.Homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("organizations").document(org).collection("Classes")
                        .document(currentClass).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //passing data to fragment
                                Bundle bundle = new Bundle();
                                bundle.putString("Subject", subject[0]);
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
