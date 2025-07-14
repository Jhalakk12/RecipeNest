package com.example.recipenest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 🔐 Check if user is already logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is signed in, skip WelcomeActivity and go directly to MainActivity
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish(); // Finish WelcomeActivity so user can't go back to it
            return;
        }

        // Initialize buttons
        Button registerButton = findViewById(R.id.register_button);
        Button signInButton = findViewById(R.id.sign_in_button);

        // Navigate to RegisterActivity
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Navigate to LoginActivity
        signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}