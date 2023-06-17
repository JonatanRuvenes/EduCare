package com.example.educare;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddClassFragment extends Fragment {

    //General data variables ***********************************************************************
    //Firestore variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //keyboard
    InputMethodManager imm;
    //ProgressDialog variables
    ProgressDialog progressDialog;

    //User variables
    static String org;
    static String userName;

    //General data variables ***********************************************************************

    static ArrayList<Boolean> days = new ArrayList<>(Collections.nCopies(7, false));
    static String subjectText;

    //Views
    EditText subject;
    Button add;
    static Button addStudents;
    Button addLesson;

    @Override
    public void onResume() {
        super.onResume();
        closeFragment(0);
        closeFragment(1);
        closeFragment(2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class, container, false);

        //getting general vars *********************************************************************
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // getting data from bundle
        Bundle args = getArguments();
        if (args != null) {
            // the fragment opened to add class not to add data
            org = args.getString("org");
            userName = args.getString("user name");

            //delete all the lists
            days = new ArrayList<>(Collections.nCopies(7, false));
            lessons = new ArrayList<>();
            Students = new ArrayList<>();
            subjectText = null;
            addStudents = null;
        }

        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("adding the class");
        progressDialog.setCancelable(false);
        //getting general vars *********************************************************************

        //Find views
        subject = view.findViewById(R.id.FRETSubject);
        addStudents = view.findViewById(R.id.FRBTNStudents);
        addLesson = view.findViewById(R.id.FRBTNLesson);
        add = view.findViewById(R.id.FRBTNAddClass);

        //Sets views
        if (subjectText != null) subject.setText(subjectText);
        subject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                subjectText = s.toString();
            }
        });

        addStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the keyboard
                imm.hideSoftInputFromWindow(subject.getWindowToken(), 0);

                //set button enabled
                addStudents.setEnabled(false);

                //Open the AddStudentToClassFragment
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                AddStudentToClassFragment fragment = new AddStudentToClassFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FContainerClassesT, fragment)
                        .addToBackStack(null).commit();
            }
        });

        addLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the keyboard
                imm.hideSoftInputFromWindow(subject.getWindowToken(), 0);

                //Open the AddLessonFragment
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                AddLessonFragment fragment = new AddLessonFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FContainerClassesT, fragment)
                        .addToBackStack(null).commit();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the keyboard
                imm.hideSoftInputFromWindow(subject.getWindowToken(), 0);

                //check for valid input
                if (subject.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "need to enter subject", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Students.size() == 0) {
                    Toast.makeText(getActivity(), "you must have student in class", Toast.LENGTH_SHORT).show();
                    return;
                }

                addDataToFirestore();
            }
        });


        return view;
    }

    //Adding all the needed data to firestore
    public void addDataToFirestore(){
        progressDialog.show();

        Map<String, Object> classData = new HashMap<>();
        classData.put("teacher", userName);
        classData.put("Students", Students);
        classData.put("Subject", subject.getText().toString());
        classData.put("Days", days);

        db.collection("organizations").document(org).collection("Classes")
                .add(classData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentId = documentReference.getId();

                        //add class to the teacher
                        db.collection("organizations").document(org)
                                .collection("Teacher").document(userName).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        ArrayList<String> classes = (ArrayList<String>) documentSnapshot.get("Classes");
                                        if (classes == null)
                                            classes = new ArrayList<>();
                                        classes.add(documentId);
                                        db.collection("organizations").document(org)
                                                .collection("Teacher").document(userName)
                                                .update("Classes", classes);
                                        closeFragment(0);
                                    }
                                });

                        //add class to the students
                        CollectionReference studentsColRef = db.collection("organizations").document(org).collection("Student");
                        for (int i = 0; i < Students.size(); i++) {
                            int finalI = i;
                            studentsColRef.document(Students.get(i)).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            ArrayList<String> classes = (ArrayList<String>) documentSnapshot.get("Classes");
                                            if (classes == null)
                                                classes = new ArrayList<>();
                                            classes.add(documentId);
                                            studentsColRef.document(Students.get(finalI)) //crash in the 'get' command
                                                    .update("Classes", classes);
                                            if (finalI == Students.size() - 1)
                                                closeFragment(1);
                                        }
                                    });
                        }

                        //add lesson to firebase
                        /*need to use thread to prevent from case when the fragment is closed before all data has been uploaded*/
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //add lesson col to firebase
                                CollectionReference LessonsColRef = documentReference.collection("Lessons");
                                for (int i = 0; i < 7; i++) {
                                    if (days.get(i)) {
                                        LessonsColRef
                                                .document((i + 1) + "")
                                                .set(new HashMap<>());
                                    }
                                }
                                final boolean[] j = {false};
                                for (int i = 0; i < lessons.size(); i++) {
                                    while (j[0]) ;
                                    j[0] = true;
                                    Map<String, Object> lessonData = new HashMap<>();
                                    lessonData.put("endHour", lessons.get(i).end.getHour());
                                    lessonData.put("endMinute", lessons.get(i).end.getMinutes());
                                    lessonData.put("startHour", lessons.get(i).start.getHour());
                                    lessonData.put("startMinute", lessons.get(i).start.getMinutes());

                                    int finalI = i;
                                    db.collection("organizations").document(org).collection("Lessons")
                                            .add(lessonData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    String documentId = documentReference.getId();

                                                    LessonsColRef.document(lessons.get(finalI).day + "").get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    ArrayList<String> lessonsID = (ArrayList<String>) documentSnapshot.get("lessonsID");
                                                                    if (lessonsID == null)
                                                                        lessonsID = new ArrayList<>();
                                                                    lessonsID.add(documentId);
                                                                    LessonsColRef.document(lessons.get(finalI).day + "")
                                                                            .update("lessonsID", lessonsID);
                                                                    if (finalI == lessons.size() - 1)
                                                                        closeFragment(2);
                                                                    j[0] = false;
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        }).start();
                    }
                });
    }


    //To reset the static variables when fragment is closed;
    boolean[] firestoreActions = new boolean[]{false, false, false};
    /**
     * 0 - add class to the teacher
     * 1 - add class to the students
     * 2 - add lesson to firebase
     **/
    public void closeFragment(int code) {
        firestoreActions[code] = true;
        for (int i = 0; i < firestoreActions.length; i++) {
            if (!firestoreActions[i]) return;
        }

        progressDialog.dismiss();

        days = new ArrayList<>(Collections.nCopies(7, false));
        lessons = new ArrayList<>();
        Students = new ArrayList<>();
        subjectText = null;
        userName = null;
        org = null;
        addStudents = null;

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(AddClassFragment.this).commit();
    }


    //Used to add students from AddStudentToClassFragment
    static ArrayList<String> Students = new ArrayList<>();

    public static void AddStudent(String student) {
        Students.add(student);
    }


    //Used to add lesson from AddLessonFragment
    static ArrayList<lesson> lessons = new ArrayList<>();

    public static void AddLesson(int day, int startHour, int startMinute, int endHour, int endMinute) {
        lessons.add(new lesson(new Time(startHour, startMinute), new Time(endHour, endMinute), day));
    }


    private static class lesson {
        public Time start;
        public Time end;
        public int day;

        public lesson(Time start, Time end, int day) {
            this.start = start;
            this.end = end;
            this.day = day;
            days.set(day - 1, true);
        }
    }
}