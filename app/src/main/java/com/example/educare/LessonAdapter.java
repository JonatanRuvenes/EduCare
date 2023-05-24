package com.example.educare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder>{
    ArrayList<Lesson> lessons;
    String tORs;


    public LessonAdapter(ArrayList<Lesson> lessons, String tORs, FragmentActivity fragmentActivity) {
        this.lessons = lessons;
        this.tORs = tORs;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View lessonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_lesson,parent,false);
        return new LessonViewHolder(lessonView);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson currentLesson = lessons.get(position);

        holder.className.setText(currentLesson.getSubject());
        holder.Time.setText(currentLesson.getStart().toString() +"-"+ currentLesson.getEnd().toString());

        // Set OnClickListener on the view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start new activity with the ClassroomID as extra
                if (tORs.equals("Teacher")){
                    Intent i = new Intent(view.getContext(), LessonTActivity.class);
                    i.putExtra("ClassroomId", lessons.get(holder.getAdapterPosition()).getClassroomID());
                    i.putExtra("Subject", lessons.get(holder.getAdapterPosition()).getSubject());
                    i.putExtra("StartHour", lessons.get(holder.getAdapterPosition()).getStart().Hour);
                    i.putExtra("StartMinutes", lessons.get(holder.getAdapterPosition()).getStart().Minutes);
                    i.putExtra("EndHour", lessons.get(holder.getAdapterPosition()).getEnd().Hour);
                    i.putExtra("EndMinutes", lessons.get(holder.getAdapterPosition()).getEnd().Minutes);
                    view.getContext().startActivity(i);
                }
                //Open Student Fragment for multiple actions
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("ClassroomId", lessons.get(holder.getAdapterPosition()).getClassroomID());
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    LessonSFragment fragment = new LessonSFragment();
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.FContainerLessonS,fragment)
                            .addToBackStack(null).commit();
                }

            }
        });
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder{

        public TextView className;
        public TextView Time;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);

            className = itemView.findViewById(R.id.RITVClassName);
            Time = itemView.findViewById(R.id.RITVTime);
        }
    }
}
