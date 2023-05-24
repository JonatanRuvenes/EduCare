package com.example.educare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class StudentTestListAdapter extends RecyclerView.Adapter<StudentTestListAdapter.StudentTestListViewHolder>{

    ArrayList<String> students;
    String org;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public StudentTestListAdapter(ArrayList<String> students, String org){
        this.students = students;
        this.org = org;
    }

    @NonNull
    @Override
    public StudentTestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View studentTestView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_studentstest,parent,false);
        return new StudentTestListViewHolder(studentTestView);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull StudentTestListViewHolder holder, int position) {
        String currentStudent = students.get(position);

        db.collection("organizations").document(org).collection("Student")
                .document(currentStudent).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        holder.name.setText(documentSnapshot.get("Name").toString());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentTestListViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        EditText grade;

        public StudentTestListViewHolder(@NonNull View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.RITVStudentNameForTest);
            grade = itemView.findViewById(R.id.RIETGrade);
        }
     }

}
