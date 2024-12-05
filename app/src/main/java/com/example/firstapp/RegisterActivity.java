package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText registerName, registerEmail, registerUsername, registerPassword;
    TextView loginDirectText;
    Button btnRegister;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerName = findViewById(R.id.signUp_name);
        registerEmail = findViewById(R.id.signUp_email);
        registerUsername = findViewById(R.id.signUp_userName);
        registerPassword = findViewById(R.id.signUp_password);
        btnRegister = findViewById(R.id.btnRegister);
        loginDirectText = findViewById(R.id.loginDirectText);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = registerName.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String userName = registerUsername.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                if (validateInputs(name, email, userName, password)) {
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");

                    // Generate a unique key for each user
                    String uniqueKey = reference.push().getKey();

                    if (uniqueKey != null) {
                        Helper helper = new Helper(name, email, userName, password);
                        reference.child(uniqueKey).setValue(helper);

                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to generate unique key", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginDirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private boolean validateInputs(String name, String email, String userName, String password) {
        if (name.isEmpty()) {
            registerName.setError("Name is required");
            return false;
        }
        if (email.isEmpty()) {
            registerEmail.setError("Email is required");
            return false;
        }
        if (userName.isEmpty()) {
            registerUsername.setError("Username is required");
            return false;
        }
        if (password.isEmpty()) {
            registerPassword.setError("Password is required");
            return false;
        }
        return true;
    }
}
