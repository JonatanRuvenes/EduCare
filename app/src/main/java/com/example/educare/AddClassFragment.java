package com.example.educare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddClassFragment extends Fragment {

    //TODO: make it work
    /*
        TODO: add class to firebase with
            teachers name
            students list
            subject
            and days list(look in firebase)
    */
    //TODO: connect the class id to the teacher and the students
    //TODO: make search for the teacher
    //TODO: generate new lesson(look in the firestore format)
    /*
        TODO: adding the parameters to the lesson
            end/start hour/minute
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_class, container, false);
    }
}