package com.example.educare;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddHomeworkFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView Subject;
    TextView dateTXT;
    ImageView Cal;
    int mDate, mMonth, mYear;

    EditText description;

    String org;
    String ClassID;
    ArrayList<String> Students;

    Button Add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_homework, container,false);

        dateTXT = view.findViewById(R.id.FRTVDate);
        Cal = view.findViewById(R.id.FRIVCalendar);
        Subject = view.findViewById(R.id.FRTVAddHomeworkSubject);

        //getting data from bundle
        Bundle args = getArguments();
        if (args != null) {
            Subject.setText(args.getString("Subject"));
            ClassID = args.getString("ClassID");
            org = args.getString("org");
        }

        ImportStudentsListFromFirestore();

        description = view.findViewById(R.id.FRETHomeworkDescription);

        Cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                mDate = cal.get(Calendar.DATE);
                mMonth = cal.get(Calendar.MONTH);
                mYear = cal.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date){
                        dateTXT.setText(date+"/"+month+"/"+year);
                    }},mYear,mMonth,mDate);
                datePickerDialog.show();
            }
        });

        Add = view.findViewById(R.id.FRBTNAddHomework);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if all data has been given
                if(description.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "you need to add description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dateTXT.getText().toString().equals("XX/XX/XXXX")) {
                    Toast.makeText(getActivity(), "you need to add submission date", Toast.LENGTH_SHORT).show();
                    return;
                }

                //adding data to firebase
                CollectionReference colRef = db.collection("organizations").document(org)
                        .collection("Student");
                for (int i = 0; i< Students.size(); i++){
                    DocumentReference docRef = colRef.document(Students.get(i)).collection("Homeworks").document(ClassID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            //importing data from firebase
                            ArrayList<String> text = (ArrayList<String>) document.get("text");
                            if (text == null) text = new ArrayList<>();
                            ArrayList<Timestamp> date = (ArrayList<Timestamp>) document.get("date");
                            if (date == null) date = new ArrayList<>();

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(mYear, mMonth, mDate);
                            date.add(new Timestamp(calendar.getTime()));
                            text.add(description.getText().toString());

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("text", text);
                            updates.put("date", date);

                            docRef.set(updates);

                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(AddHomeworkFragment.this).commit();
                        }
                    });
                }
            }
        });


        return view;
    }

    private void ImportStudentsListFromFirestore() {
        db.collection("organizations").document(org).collection("Classes")
                .document(ClassID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Object existingClassesArray = documentSnapshot.get("Students");
                        if (existingClassesArray instanceof List) {
                            Students = (ArrayList<String>) documentSnapshot.get("Students");
                        }
                    }
                });

    }
}