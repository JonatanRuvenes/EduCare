package com.example.educare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

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

    String TAG = "check";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        savedUserData = getSharedPreferences("UserData", MODE_PRIVATE);
        UserData = savedUserData.edit();

        if (
                savedUserData.getString("org", "nope") != "nope" ||
                savedUserData.getString("tORs", "nope") != "nope" ||
                savedUserData.getString("UserName", "nope") != "nope"
        ) {
            org  =savedUserData.getString("org", "nope");
            tORs =savedUserData.getString("tORs", "nope");
            Name =savedUserData.getString("UserName", "nope");
            if (tORs.equals("Student")) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 111);
                } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 112);
                } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 113);
                }
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    db.collection("organizations").document(org)
                            .collection("Student").document(Name)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    ArrayList<String> classesIDs = (ArrayList<String>) documentSnapshot.get("Classes");
                                    if (classesIDs == null) classesIDs = new ArrayList<>();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    for (String id : classesIDs) {
                                        database.getReference(org).child(id).child("Teacher")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Iterable<DataSnapshot> children = snapshot.getChildren();
                                                        for (DataSnapshot child : children) {
                                                            try {
                                                                Long latitudeLong = child.child("Latitude").getValue(Long.class);
                                                                Long longitudeLong = child.child("Longitude").getValue(Long.class);
                                                                double latitude = latitudeLong != null ? latitudeLong.doubleValue() : 0.0;
                                                                double longitude = longitudeLong != null ? longitudeLong.doubleValue() : 0.0;
                                                                UserLocation TeacherLocation = new UserLocation(latitude, longitude);

                                                                final UserLocation[] studentLocation = new UserLocation[1];

                                                                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(SignInActivity.this);
                                                                if (ActivityCompat.checkSelfPermission(SignInActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignInActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
                                                                fusedLocationClient.getLastLocation()
                                                                        .addOnSuccessListener(SignInActivity.this, new OnSuccessListener<Location>() {
                                                                            @Override
                                                                            public void onSuccess(Location location) {
                                                                                if (location != null) {

                                                                                    studentLocation[0] = new UserLocation(location.getLatitude(), location.getLongitude());
                                                                                    Log.d("getLatitude", studentLocation[0].getLatitude()+"");
                                                                                    Log.d("getLongitude", studentLocation[0].getLongitude()+"");

                                                                                    if (TeacherLocation.isWithinRange(studentLocation[0])|| true){
                                                                                        //TODO: delete the true statement
                                                                                        database.getReference(org).child(id).child("Students").child(Name).setValue(Name);
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                            } catch (Exception e) {
                                                                Log.d("error", e.toString());
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {}
                                                });
                                    }
                                }
                            });
                }

            }

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
                                                Intent i =new Intent(getApplicationContext(), SignInActivity.class);
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