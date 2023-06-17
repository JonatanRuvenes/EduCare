package com.example.educare;

import java.util.Calendar;

public class Time {
    int Hour;
    int Minutes;

    public Time(int hour, int minutes) {
        Hour = hour;
        Minutes = minutes;
        addTime(0,0);
    }

    public void addTime(int hour, int minutes){
        Hour += hour;
        Minutes += minutes;
        while(Minutes >= 60) {
            Hour++;
            Minutes-=60;
        }
        while(Hour >= 24) Hour -= 24;
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        Hour = hour;
    }

    public int getMinutes() {
        return Minutes;
    }

    public void setMinutes(int minutes) {
        Minutes = minutes;
    }

    public Calendar subtractMinutes(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, this.Hour);
        calendar.set(Calendar.MINUTE, this.Minutes);
        calendar.add(Calendar.MINUTE, -minutes);
        return calendar;
    }

    @Override
    public String toString() {
        String hour;
        if (Hour < 10) hour = "0"+Hour;
        else hour = ""+Hour;
        String minute;
        if (Minutes < 10) minute = "0"+Minutes;
        else minute = ""+Minutes;
        return hour+":"+minute;
    }
}
