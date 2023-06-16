package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //General data variables ***********************************************************************
    //SharedPreferences variables
    SharedPreferences UserData;

    //General data variables ***********************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting general vars *********************************************************************
        UserData = getSharedPreferences("UserData", MODE_PRIVATE);
        if(UserData.getBoolean("isFirstLaunch", true)){
            scheduleJob();
            SharedPreferences.Editor editor = UserData.edit();
            editor.putBoolean("isFirstLaunch", false);
        }
        //getting general vars *********************************************************************

        Intent i =new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(i);
    }

    public void scheduleJob(){
        // Create a Calendar object and set it to 9 AM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);


        // Create an Intent for the BroadcastReceiver
        Intent intent = new Intent(this, DownloadTodayLessonsAlarmReceiver.class);
        if (UserData.getString("org", "not found").equals("not found"))
        intent.putExtra("org",UserData.getString("org", "not found"));
        intent.putExtra("tORs",UserData.getString("tORs", "not found"));
        intent.putExtra("UserName",UserData.getString("UserName", "not found"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager service and set the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}