package com.example.educare;

public class User {

    public String ID;
    public String Email;
    public String Name;
    public String stu_teach;

    public String picture;

    public User(String ID, String email, String name, String Stu_Teach) {
        this.ID = ID;
        Email = email;
        Name = name;
        stu_teach = Stu_Teach;
        picture = null;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStu_teach() {
        return stu_teach;
    }

    public void setStu_teach(String stu_teach) {
        this.stu_teach = stu_teach;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

