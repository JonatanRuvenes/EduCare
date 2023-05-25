package com.example.educare;

public class TestOutput extends Test {
    String date;

    public TestOutput(String testName, int grade, String subject, String teachers_name, String date) {
        super(testName, grade, subject, teachers_name);
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
