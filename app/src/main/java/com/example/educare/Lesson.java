package com.example.educare;

public class Lesson {
    String subject;
    Time Start;
    Time End;
    String ClassroomID;


    public Lesson(String className, Time start, Time end, String classroomID) {
        this.subject = className;
        Start = start;
        End = end;
        ClassroomID = classroomID;
    }

    public String getClassroomID() {
        return ClassroomID;
    }

    public void setClassroomID(String classroomID) {
        ClassroomID = classroomID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Time getStart() {
        return Start;
    }

    public void setStart(Time start) {
        Start = start;
    }

    public Time getEnd() {
        return End;
    }

    public void setEnd(Time end) {
        End = end;
    }
}
