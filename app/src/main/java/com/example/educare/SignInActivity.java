package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {

    SharedPreferences savedUserData;

    SharedPreferences.Editor UserData;

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String UID;
    String org;
    String tORs;
    String Name;

    TextView Email;
    TextView Password;
    Button enter;
    Button signUp;

    String TAG  = "check";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        savedUserData = getSharedPreferences("UserData", MODE_PRIVATE);
        UserData = savedUserData.edit();

        if (
                savedUserData.getString("org", "nope")  != "nope" ||
                savedUserData.getString("tORs", "nope")  != "nope" ||
                savedUserData.getString("UserName", "nope")  != "nope"
        ){
            Intent i =new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(i);
        }

        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.ETSignInEmail);
        Password = findViewById(R.id.ETPassword);

        signUp = findViewById(R.id.BTNSignUp);
        enter = findViewById(R.id.BTNEnter);

        signUp.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(i);
        });

        enter.setOnClickListener(view -> { 
            if (Password.getText().toString().equals("") || Email.getText().toString().equals(""))
                Toast.makeText(SignInActivity.this, "not all the fields are  ", Toast.LENGTH_SHORT).show();
            else{
                //Connecting user using Authentication
                Log.d(Email.getText().toString(), Password.getText().toString());
                mAuth.signInWithEmailAndPassword(Email.getText().toString(), Password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("log in", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UID = user.getUid().toString();

                                    DocumentReference docRef = db.collection("Users").document(UID);
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                //Taking data from firestore
                                                org = documentSnapshot.getString("org");
                                                tORs = documentSnapshot.getString("tORs");
                                                Name = documentSnapshot.getString("Name");

                                                //adding data to SharedPreferences
                                                UserData.putString("org",org);
                                                UserData.putString("UserName", Name);
                                                UserData.putString("tORs" , tORs);
                                                UserData.apply();

                                                //Move to HomePageActivity
                                                Intent i =new Intent(getApplicationContext(), HomePageActivity.class);
                                                startActivity(i);
                                            }else{
                                                Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("log in", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}