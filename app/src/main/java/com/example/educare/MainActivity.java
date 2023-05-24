package com.example.educare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    SharedPreferences.Editor UserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserData = getSharedPreferences("UserData", MODE_PRIVATE).edit();

        UserData.putString("org", "kiryat noar");
        UserData.putString("UserName", "hezi");
        UserData.putString("tORs" , "Teacher");
        UserData.apply();

        Intent i =new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(i);
    }
}