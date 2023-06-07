package com.example.educare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder>{
    ArrayList<User> students;

    public StudentsAdapter(ArrayList<User> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View studentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_student,parent,false);
        return new StudentsViewHolder(studentView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsViewHolder holder, int position) {
        User currentStudent = students.get(position);

        holder.Name.setText(currentStudent.getName());


    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentsViewHolder extends RecyclerView.ViewHolder{

        TextView Name;

        public StudentsViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.RITVStudentName);
        }
    }
}
