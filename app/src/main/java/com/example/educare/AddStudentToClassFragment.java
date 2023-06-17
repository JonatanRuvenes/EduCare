package com.example.educare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddStudentToClassFragment extends Fragment {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Activity variables
    StudentsToClassAdapter studentsToClassAdapter;
    ArrayList<String> StudentsList = new ArrayList<>();

    //User variables
    String org;

    //General data variables ***********************************************************************

    //Views
    RecyclerView students;
    Button add;

    @Override
    public void onResume() {
        super.onResume();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(AddStudentToClassFragment.this).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student_to_class, container, false);

        //getting general vars *********************************************************************
        org = AddClassFragment.org;

        //getting general vars *********************************************************************

        //Find views
        students = view.findViewById(R.id.FRRVStudentsToClass);
        add = view.findViewById(R.id.FRBTNEndAddingStudents);

        //Sets views
        findStudentsInFirestore();

        add.setOnClickListener(new View.OnClickListener() {
            //come back to AddClassFragment
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

    public void findStudentsInFirestore(){
        db.collection("organizations").document(org).collection("Student").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentName = documentSnapshot.getId();
                            StudentsList.add(documentName);
                        }
                        updateStudentsList();
                    }
                });
    }

    //Update the students views in the students RecyclerView
    private void updateStudentsList() {
        RecyclerView.LayoutManager studentsToClassLayout = new LinearLayoutManager(getContext());
        studentsToClassAdapter = new StudentsToClassAdapter();
        students.setLayoutManager(studentsToClassLayout);
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
            String currentStudent = StudentsList.get(position);

            holder.name.setText(currentStudent);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddClassFragment.AddStudent(currentStudent);
                    StudentsList.remove(currentStudent);
                    StudentsList.remove(currentStudent);
                    studentsToClassAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return StudentsList.size();
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