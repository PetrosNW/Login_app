package com.example.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginUserName, loginPassword;
    Button btnLogin;
    TextView registerDirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        loginPassword = findViewById(R.id.login_password);
        loginUserName = findViewById(R.id.login_userName);
        btnLogin = findViewById(R.id.btnLogin);
        registerDirectText = findViewById(R.id.registerDirectText);

        // Login button listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserName() && validatePassword()) {
                    checkUser();
                }
            }
        });

        // Redirect to register page
        registerDirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    // Validate username
    public Boolean validateUserName() {
        String val = loginUserName.getText().toString().trim();
        if (val.isEmpty()) {
            loginUserName.setError("Username cannot be empty");
            return false;
        } else {
            loginUserName.setError(null);
            return true;
        }
    }

    // Validate password
    public Boolean validatePassword() {
        String val = loginPassword.getText().toString().trim();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    // Check user in the database
    public void checkUser() {
        String userUserName = loginUserName.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUserName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch user data
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String passwordFromDb = userSnapshot.child("password").getValue(String.class);

                    if (passwordFromDb != null && passwordFromDb.equals(userPassword)) {
                        String nameFromDB = userSnapshot.child("name").getValue(String.class);
                        String emailFromDB = userSnapshot.child("email").getValue(String.class);
                        String usernameFromDB = userSnapshot.child("username").getValue(String.class);

                        // Redirect to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("password", passwordFromDb);
                        intent.putExtra("username", usernameFromDB);
                        startActivity(intent);
                    } else {
                        loginPassword.setError("Invalid credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUserName.setError("User does not exist");
                    loginUserName.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
