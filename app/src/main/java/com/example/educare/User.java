package com.example.educare;

public class User {
    public String id;
    public String email;
    public String name;
    public String stu_teach;

    public User(String id, String email, String name, String stu_teach) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.stu_teach = stu_teach;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStu_teach() {
        return stu_teach;
    }

    public void setStu_teach(String stu_teach) {
        this.stu_teach = stu_teach;
    }
}