package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class HomePageActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences variables
    SharedPreferences UserData;

    //ProgressDialog variables
    ProgressDialog progressDialog;

    //Activity variables
    int day;
    Calendar calendar = Calendar.getInstance();
    ArrayList<String> Classes = new ArrayList<>();
    public static ArrayList<Lesson> lessons;

    //User variables
    String org;
    String tORs;
    String UserName;

    //General data variables ***********************************************************************

    //Views
    TextView Name;
    RecyclerView timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //getting general vars *********************************************************************
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        tORs = UserData.getString("tORs", "not found");
        UserName = UserData.getString("UserName", "not found");
        day = calendar.get(Calendar.DAY_OF_WEEK);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading today classes");
        progressDialog.setCancelable(true);
        //getting general vars *********************************************************************

        //Find views
        Name = findViewById(R.id.TVName);
        timetable = findViewById(R.id.RVTimetable);

        //Sets views

        Name.setText(UserName);

        updateTimeTable();

    }

    public void updateTimeTable() {
        if (lessons == null) return;
        Log.d("update", "updateTimeTable: ");
        RecyclerView.LayoutManager timetableLayout = new LinearLayoutManager(HomePageActivity.this);
        LessonAdapter timetableAdapter = new LessonAdapter(lessons,tORs,HomePageActivity.this);
        timetable.setLayoutManager(timetableLayout);
        timetable.setAdapter(timetableAdapter);
        progressDialog.dismiss();
    }

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

}