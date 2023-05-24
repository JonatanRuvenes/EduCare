package com.example.educare;

public class Test {
    String TestName;
    int grade;
    String Subject;
    String teachers_name;

    public Test(String testName, int grade, String subject, String teachers_name) {
        TestName = testName;
        this.grade = grade;
        Subject = subject;
        this.teachers_name = teachers_name;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getTeachers_name() {
        return teachers_name;
    }

    public void setTeachers_name(String teachers_name) {
        this.teachers_name = teachers_name;
    }
}

