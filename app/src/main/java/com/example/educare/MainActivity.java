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
        /*
            email: jonatan3705@gmail.com
            password: 123456
            //Student

            email: hezi@gmail.com
            password: 123456
            //Teacher
        */
        Intent i =new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(i);
    }
}