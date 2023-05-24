package com.example.educare;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonTActivity extends AddMenuActivity {

    //private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference databaseRef;
    private static final int LOCATION_PERMISSION_CODE = 231;


    SharedPreferences UserData;
    String org;
    String SubjectName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Timestamp lessonTime;
    Map<String, Object> dateMap = new HashMap<>();
    Map<String, Object> classNameMap = new HashMap<>();
    ArrayList<Student> students = new ArrayList<>();
    String ClassroomID;

    RecyclerView studentsList;
    TextView Subject;
    Button update;
    Button findData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_tactivity);

        UserData = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        org = UserData.getString("org", "not found");

        Intent i = getIntent();
        ClassroomID = i.getStringExtra("ClassroomId");
        SubjectName = i.getStringExtra("Subject");

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Get a reference to the root node
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        databaseRef = rootRef.child(ClassroomID);

        Subject = findViewById(R.id.TVSubject);
        db.collection("organizations").document(org).collection("Classes")
                .document(ClassroomID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Subject.setText((String) documentSnapshot.get("Subject"));
                    }
                });

        update = findViewById(R.id.BTNUpdateStudentsData);
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
            }});

        findData = findViewById(R.id.BTNFindData);
        findData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: check why its not working
                /*need to make that the teacher will find where is the student */
                //getLocation();
            }
        });



        studentsList = findViewById(R.id.RVStudentsList);
        updateStudents();
    }

    public void updateStudents(){
        //finding data from firebase
        DocumentReference docRef = db.collection("organizations").document(org)
                .collection("Classes").document(ClassroomID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> studentsNames = (ArrayList<String>) documentSnapshot.get("Students");
                for (int i=0; i<studentsNames.size();i++)
                    students.add(new Student(studentsNames.get(i)));

                RecyclerView.LayoutManager studentsListLayout = new LinearLayoutManager(LessonTActivity.this);
                StudentsListAdapter studentsListAdapter = new StudentsListAdapter(students , org);
                studentsList.setLayoutManager(studentsListLayout);
                studentsList.setAdapter(studentsListAdapter);
            }
        });
    }


    /*private ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (result != null) {
                        boolean fine = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                        boolean coarse = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                        // this means permission has been approved
                        if (fine && coarse) {
                            // this method handles locations
                            getLocation();


                            Toast.makeText(LessonTActivity.this, "Location Permission approved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LessonTActivity.this, "App cannot work without location approval", Toast.LENGTH_SHORT).show();
                            LessonTActivity.this.finish();
                        }


                    } else {
                        Toast.makeText(LessonTActivity.this, "App cannot work without location approval", Toast.LENGTH_SHORT).show();
                        LessonTActivity.this.finish();
                    }


                }

            });
    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissionLauncher.launch(permissions);

        }
        else{

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationProviderClient.getCurrentLocation(100,null)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!=null)
                            {
                                double lat = location.getLatitude();
                                double lon = location.getLongitude();

                                Toast.makeText(LessonTActivity.this, "location = " + lat + "," + lon, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }

    }*/
}