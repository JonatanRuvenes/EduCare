package com.example.educare;

import com.google.firebase.Timestamp;

public class TestInput extends Test{
    String ClassID;
    Timestamp date;

    public TestInput(String testName, int grade, String subject, String teachers_name, String classID, Timestamp date) {
        super(testName, grade, subject, teachers_name);
        ClassID = classID;
        this.date = date;
    }
}
