package com.example.educare;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonTActivity extends AddMenuActivity {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //RealtimeDatabase variables
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    //RealtimeDatabase variables
    SharedPreferences UserData;

    //Activity variables
    Timestamp lessonTime;
    ArrayList<Student> students = new ArrayList<>();
    StudentsListAdapter studentsListAdapter;

    //User variables
    String org;
    String SubjectName;
    String ClassroomID;

    //General data variables ***********************************************************************

    //Views
    RecyclerView studentsList;
    TextView Subject;
    Button update;
    Button findStudents;

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_tactivity);
        //getting general vars *********************************************************************
        //getting data from SharedPreferences
        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");
        //getting data from Intent
        Intent i = getIntent();
        ClassroomID = i.getStringExtra("ClassroomId");
        SubjectName = i.getStringExtra("Subject");

        myRef = database.getReference(org).child(ClassroomID);
        //getting general vars *********************************************************************

        //Find views
        Subject = findViewById(R.id.TVSubject);
        update = findViewById(R.id.BTNUpdateStudentsData);
        studentsList = findViewById(R.id.RVStudentsList);
        findStudents = findViewById(R.id.BTNFindData);

        //Sets views
        db.collection("organizations").document(org).collection("Classes")
                .document(ClassroomID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Subject.setText((String) documentSnapshot.get("Subject"));
                    }
                });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 15);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                lessonTime = new Timestamp(calendar.getTime());
                for (int i = 0; i < students.size(); i++) {
                    //Adding Attendance to Student
                    if (!students.get(i).Attendance) {
                        CollectionReference colRef = db.collection("organizations").document(org)
                                .collection("Student").document(students.get(i).getName())
                                .collection("disturbance");
                        int finalI = i;

                        DocumentReference docRefUnShow = colRef.document("unShow");
                        docRefUnShow.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Object existingDatesArray = documentSnapshot.get("date");
                                    if (existingDatesArray instanceof List) {
                                        List<Object> dates = (List<Object>) existingDatesArray;
                                        List<Object> Classes = (List<Object>) documentSnapshot.get("Class");
                                        dates.add(lessonTime);
                                        Classes.add(ClassroomID);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("date", dates);
                                        data.put("Class", Classes);
                                        docRefUnShow.set(data);
                                    }
                                } else {
                                    // The document does not exist
                                    ArrayList<Object> dates = new ArrayList<>();
                                    ArrayList<Object> Classes = new ArrayList<>();
                                    dates.add(lessonTime);
                                    Classes.add(ClassroomID);
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("date", dates);
                                    data.put("Class", Classes);

                                    db.collection("organizations").document(org)
                                            .collection("Student").document(students.get(finalI).getName())
                                            .collection("disturbance").document("unShow")
                                            .set(data);
                                }
                            }
                        });
                    }
                    //Adding Homework to Student
                    if (!students.get(i).DidHomeWork) {
                        CollectionReference colRef = db.collection("organizations").document(org)
                                .collection("Student").document(students.get(i).getName())
                                .collection("disturbance");
                        int finalI = i;

                        DocumentReference docRefNoHomeWork = colRef.document("noHomeWork");
                        docRefNoHomeWork.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Object existingDatesArray = documentSnapshot.get("date");
                                    if (existingDatesArray instanceof List) {
                                        List<Object> dates = (List<Object>) existingDatesArray;
                                        List<Object> Classes = (List<Object>) documentSnapshot.get("Class");
                                        dates.add(lessonTime);
                                        Classes.add(ClassroomID);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("date", dates);
                                        data.put("Class", Classes);
                                        docRefNoHomeWork.set(data);
                                    }
                                } else {
                                    // The document does not exist
                                    ArrayList<Object> dates = new ArrayList<>();
                                    ArrayList<Object> Classes = new ArrayList<>();
                                    dates.add(lessonTime);
                                    Classes.add(ClassroomID);
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("date", dates);
                                    data.put("Class", Classes);

                                    db.collection("organizations").document(org)
                                            .collection("Student").document(students.get(finalI).getName())
                                            .collection("disturbance").document("noHomeWork")
                                            .set(data);
                                }
                            }
                        });
                    }
                }
                finish();
            }
        });

        updateStudents();

        findStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

    }


    /* This method find the teachers location and send it to all of the students by
    that the user find the distance between him and the teacher and returns if the
    student is in reasonable distance from the teacher and if it is the teacher set
    the student as Attendance in lesson */
    FusedLocationProviderClient fusedLocationClient;
    public void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 111);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 112);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 113);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Toast.makeText(LessonTActivity.this, "Latitude:" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LessonTActivity.this, "Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                        if (childSnapshot.getKey().equals("Students")) {
                                            for (DataSnapshot studentSnapshot : childSnapshot.getChildren()) {
                                                String student = studentSnapshot.getValue(String.class);
                                                addAttendanceToStudent(student);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                            myRef.child("Teacher").setValue(userLocation);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(5000);
                                        myRef.child("Teacher").setValue(new UserLocation(0, 0));
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }).start();
                        } else {
                            Toast.makeText(LessonTActivity.this, "having problem find your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateStudents() {
        //finding data from firebase
        DocumentReference docRef = db.collection("organizations").document(org)
                .collection("Classes").document(ClassroomID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> studentsNames = (ArrayList<String>) documentSnapshot.get("Students");
                for (int i = 0; i < studentsNames.size(); i++)
                    students.add(new Student(studentsNames.get(i)));

                RecyclerView.LayoutManager studentsListLayout = new LinearLayoutManager(LessonTActivity.this);
                studentsListAdapter = new StudentsListAdapter(students, org);
                studentsList.setLayoutManager(studentsListLayout);
                studentsList.setAdapter(studentsListAdapter);
            }
        });
    }

    public void addAttendanceToStudent(String student) {
        for (int i = 0; i < StudentsListAdapter.students.size(); i++) {
            if (StudentsListAdapter.students.get(i).Name.equals(student)) {
                StudentsListAdapter.students.get(i).Attendance = true;
                RecyclerView.LayoutManager layoutManager = studentsList.getLayoutManager();
                StudentsListAdapter.holders.get(i).Attendance.setText("UnShow");
            }
        }
    }

    public class Student {
        String Name;
        Boolean Attendance;
        Boolean DidHomeWork;

        public Student(String name) {
            Name = name;
            Attendance = false;
            DidHomeWork = false;
        }

        public void changeAttendance(){Attendance = !Attendance;}

        public void changeHomeWork(){DidHomeWork = !DidHomeWork;}

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }
    private class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.StudentsListViewHolder> {
        static ArrayList<Student> students;
        String org;
        static ArrayList<StudentsListAdapter.StudentsListViewHolder> holders = new ArrayList<StudentsListAdapter.StudentsListViewHolder>();

        public StudentsListAdapter(ArrayList<Student> students, String org) {
            holders = new ArrayList<>();
            this.students = students;
            this.org = org;
        }

        @NonNull
        @Override
        public StudentsListAdapter.StudentsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View studentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleritem_student, parent, false);
            return new StudentsListAdapter.StudentsListViewHolder(studentView);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentsListAdapter.StudentsListViewHolder holder, int position) {
            holders.add(holder);
            Student currentStudent = students.get(position);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docRef = db.collection("organizations")
                    .document(org).collection("Student")
                    .document(currentStudent.getName());

            holder.Name.setText(currentStudent.getName());

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

        public static class StudentsListViewHolder extends RecyclerView.ViewHolder {
            public TextView Name;
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
}