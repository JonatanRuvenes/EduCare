package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    //TODO: add loading screen;

    SharedPreferences.Editor UserData;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userUID;

    Button SignUp;

    Switch Teacher_Student;
    EditText ID;
    EditText Name;
    EditText ETEmail;
    EditText Password1;
    EditText Password2;
    EditText Organization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        UserData = getSharedPreferences("UserData", MODE_PRIVATE).edit();

        mAuth = FirebaseAuth.getInstance();

        Teacher_Student = findViewById(R.id.SWTeacher_Student);
        Teacher_Student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (Teacher_Student.getText().toString()){
                    case "Teacher":
                        Teacher_Student.setText("Student");
                        break;
                    case "Student":
                        Teacher_Student.setText("Teacher");
                }
            }
        });

        ID = findViewById(R.id.ETID);
        Name = findViewById(R.id.ETSighInUserName);
        ETEmail = findViewById(R.id.ETEmail);
        Password1 = findViewById(R.id.ETSighInPassword1);
        Password2 = findViewById(R.id.ETSighInPassword2);
        Organization = findViewById(R.id.ETOrganizationName);

        SignUp = findViewById(R.id.BTNaddUser);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {

                //Check if input is correct
                if (Name.getText().toString().equals("") || Password1.getText().toString().equals("") || ID.getText().toString().equals("") ||
                        ETEmail.getText().toString().equals("") || Organization.getText().toString().equals("") || Password2.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "not all the fields are full", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Password1.getText().toString().equals(Password2.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "the two passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Password1.getText().toString().length() < 6){
                    Toast.makeText(SignUpActivity.this, "the password must be ut least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Create User
                String Email = ETEmail.getText().toString();
                String Password = Password1.getText().toString();

                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userUID = user.getUid();
                                    Toast.makeText(SignUpActivity.this, userUID+"", Toast.LENGTH_SHORT).show();

                                    //Add User To Users List
                                    Map<String, Object> userToLUsersList = new HashMap<>();
                                    userToLUsersList.put("org", Organization.getText().toString());
                                    userToLUsersList.put("tORs",Teacher_Student.getText());
                                    userToLUsersList.put("Name",Name.getText().toString());

                                    db.collection("Users").document(userUID).set(userToLUsersList)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //Add User to Organization
                                                    User userToOrganization = new User(ID.getText().toString(), ETEmail.getText().toString(), Name.getText().toString(), Teacher_Student.getText().toString());
                                                    db.collection("organizations").document(Organization.getText().toString())
                                                            .collection(Teacher_Student.getText().toString()).document(Name.getText().toString())
                                                            .set(userToOrganization);//line 127

                                                    //adding data to SharedPreferences
                                                    UserData.putString("org", Organization.getText().toString());
                                                    UserData.putString("UserName", Name.getText().toString());
                                                    UserData.putString("tORs" , Teacher_Student.getText().toString());
                                                    UserData.apply();

                                                    //Move to HomePageActivity
                                                    Intent i =new Intent(getApplicationContext(), SignInActivity.class);
                                                    startActivity(i);
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    task.getException().printStackTrace();
                                }
                            }
                        });
            }
        });
    }
}