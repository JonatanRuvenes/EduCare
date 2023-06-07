package com.example.educare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.StudentsListViewHolder>{
    ArrayList<Student> students;
    String org;

    public StudentsListAdapter(ArrayList<Student> students, String org) {
        this.students = students;
        this.org = org;
    }

    @NonNull
    @Override
    public StudentsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View studentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_student,parent,false);
        return new StudentsListViewHolder(studentView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsListViewHolder holder, int position) {
        Student currentStudent = students.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("organizations")
                .document(org).collection("Student")
                .document(currentStudent.getName());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //Taking data from firestore
                    holder.Name.setText(documentSnapshot.getString("Name"));;
                }
            }
        });

        holder.Attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                students.get(holder.getAdapterPosition()).changeAttendance();
                if (holder.Attendance.getText().toString().equals("Attendance"))
                    holder.Attendance.setText("UnShow");
                else holder.Attendance.setText("Attendance");
            }
        });

        holder.HomeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                students.get(holder.getAdapterPosition()).changeHomeWork();
                if (holder.HomeWork.getText().toString().equals("HomeWork"))
                    holder.HomeWork.setText("no HomeWork");
                else holder.HomeWork.setText("HomeWork");
            }
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentsListViewHolder extends RecyclerView.ViewHolder{
        public TextView Name;
        public ImageView profilePic;
        public Button HomeWork;
        public Button Attendance;

        public StudentsListViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.RITVStudentName);

            Attendance = itemView.findViewById(R.id.BTNAttendance);
            HomeWork = itemView.findViewById(R.id.BTNHomeWork);
        }
    }
}
