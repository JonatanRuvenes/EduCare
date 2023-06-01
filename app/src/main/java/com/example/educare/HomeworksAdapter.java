package com.example.educare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
