package com.example.educare;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Homework {
    Timestamp TimeDate;
    String date;
    String text;

    public Homework(Timestamp timeDate, String text) {
        TimeDate = timeDate;
        Date date = timeDate.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = sdf.format(date);
        this.text = text;
    }

    public Timestamp getTimeDate() {
        return TimeDate;
    }

    public void setTimeDate(Timestamp timeDate) {
        TimeDate = timeDate;
        Date date = timeDate.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = sdf.format(date);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
