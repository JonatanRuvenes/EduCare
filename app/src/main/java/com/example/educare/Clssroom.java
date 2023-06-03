package com.example.educare;

import java.util.ArrayList;

public class Clssroom {
    String ClassName;

    ArrayList<User> students;


    public Clssroom(String className) {
        ClassName = className;
        this.students = new ArrayList<>();
    }

    public void addStudent(User student){
        if (student.stu_teach == "Teacher") return;
        students.add(student);

        sortStudentsList();
    }
    public void sortStudentsList(){
        ArrayList<User> sortedList = new ArrayList<>();
        int index = 0;
        while (!students.isEmpty()){
            for(int i = 1; i<students.size();i++){
                index = findMinStudent(index,i);
            }
            sortedList.add(students.get(index));
            students.remove(index);
            index = 0;
        }
        students = sortedList;
    }

    public int findMinStudent(int a, int b){
        if (students.get(a).getName().toLowerCase().compareTo(students.get(b).getName().toLowerCase()) > 0) return b;
        return a;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public ArrayList<User> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<User> students) {
        this.students = students;
    }
}
