package com.example.educare;

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
