package com.example.educare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import org.checkerframework.checker.nullness.qual.NonNull;

//Its not a real Activity its just for adding the menu
//each Activity that have Menu extending this Activity instead of AppCompatActivity
public class AddMenuActivity extends AppCompatActivity {

    Intent i;
    SharedPreferences UserData;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        UserData = getSharedPreferences("UserData", MODE_PRIVATE);
//
//        switch (item.getItemId()){
//            case R.id.MIHome:
//                i =new Intent(getApplicationContext(), HomePageActivity.class);
//                startActivity(i);
//                return true;
//            case R.id.MILogOut:
//                SharedPreferences.Editor EUserData = UserData.edit();
//                EUserData.remove("org");
//                EUserData.remove("UserName");
//                EUserData.remove("tORs");
//                i =new Intent(getApplicationContext(), SignInActivity.class);
//                startActivity(i);
//                return true;
//            case R.id.MIClasses:
//                if(UserData.getString("tORs", "not found").equals("Teacher")){
//                    i = new Intent(getApplicationContext(), ClassesTActivity.class);
//                    startActivity(i);
//                }
//                else{
//                    // TODO: add ClassesSActivity
//                }
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
