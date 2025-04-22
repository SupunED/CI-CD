package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;

public class Register extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextBirthday, editTextPassword;
    TextView textView;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    // check if the user is already signed in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        Button regButton = findViewById(R.id.regButton);
        editTextUsername = findViewById(R.id.reg_username);
        editTextEmail = findViewById(R.id.reg_email);
        editTextBirthday = findViewById(R.id.reg_birthday);
        editTextPassword = findViewById(R.id.reg_password);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);


        // navigate to login page
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // register button click event listener
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String username, email, birthday, password;

                username = String.valueOf(editTextUsername.getText());
                email = String.valueOf(editTextEmail.getText());
                birthday = String.valueOf(editTextBirthday.getText());
                password = String.valueOf(editTextPassword.getText());

                if (email.isEmpty() || password.isEmpty() || username.isEmpty() || birthday.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }



                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    User newUser = new User(username, email, birthday);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(user.getUid())
                                            .setValue(newUser)
                                                    .addOnCompleteListener(dbTask ->{
                                                        if (dbTask.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Toast.makeText(Register.this, "Account Created.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                                            startActivity(intent);
                                                            finish();
                                                            // FirebaseUser user = mAuth.getCurrentUser();
                                                        }
                                                        else {
                                                            Toast.makeText(Register.this, "Database Error.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }

                                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });


            }
        });
    }


    public static class User {

        public String username, email, birthday;

        public User () {}

        public User(String username, String email, String birthday) {
            this.username = username;
            this.email = email;
            this.birthday = birthday;
        }

    }

}