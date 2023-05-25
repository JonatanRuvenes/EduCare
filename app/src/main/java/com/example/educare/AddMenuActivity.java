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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        UserData = getSharedPreferences("UserData", MODE_PRIVATE);

        if (item.getItemId() == R.id.MIHome) {
            i = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.MILogOut) {
            SharedPreferences.Editor EUserData = UserData.edit();
            EUserData.remove("org");
            EUserData.remove("UserName");
            EUserData.remove("tORs");
            EUserData.apply();
            i = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(i);
            finish(); // Optional: Finish the current activity
            return true;
        } else if (item.getItemId() == R.id.MIClasses) {
            if (UserData.getString("tORs", "not found").equals("Teacher")) {
                i = new Intent(getApplicationContext(), ClassesTActivity.class);
                startActivity(i);
            } else {
                // TODO: Handle the case for student menu item selection
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
