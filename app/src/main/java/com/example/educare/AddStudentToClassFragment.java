package com.example.educare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddStudentToClassFragment extends Fragment {

    //TODO: make search for the teacher

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String org;
    StudentsToClassAdapter studentsToClassAdapter;
    ArrayList<String> Students = new ArrayList<>();
    ArrayList<String> StudentsSearch;
    RecyclerView students;
    Button add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student_to_class, container, false);

        org = AddClassFragment.org;

        students = view.findViewById(R.id.FRRVStudentsToClass);
        db.collection("organizations").document(org).collection("Student").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String documentName = documentSnapshot.getId();
                                    Students.add(documentName);
                                }
                                StudentsSearch = Students;
                                updateStudentsList();
                            }
                        });

        add = view.findViewById(R.id.FRBTNEndAddingStudents);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                AddClassFragment fragment = new AddClassFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FContainerClassesT,fragment)
                        .addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void updateStudentsList() {
        RecyclerView.LayoutManager timetableLayout = new LinearLayoutManager(getContext());
        studentsToClassAdapter = new StudentsToClassAdapter();
        students.setLayoutManager(timetableLayout);
        students.setAdapter(studentsToClassAdapter);
    }

    private class StudentsToClassAdapter extends RecyclerView.Adapter<StudentsToClassAdapter.StudentsToClassViewHolder>{
        @NonNull
        @Override
        public StudentsToClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View studentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_studentsname,parent,false);
            return new StudentsToClassViewHolder(studentView);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentsToClassViewHolder holder, int position) {
            String currentStudent = StudentsSearch.get(position);

            holder.name.setText(currentStudent);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddClassFragment.AddStudent(currentStudent);
                    StudentsSearch.remove(currentStudent);
                    Students.remove(currentStudent);
                    studentsToClassAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return StudentsSearch.size();
        }


        public static class StudentsToClassViewHolder extends RecyclerView.ViewHolder{
            TextView name;
            public StudentsToClassViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.RITVStudentNameForClass);
            }
        }
    }
}