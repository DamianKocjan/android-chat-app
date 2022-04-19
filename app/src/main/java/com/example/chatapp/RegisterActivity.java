package com.example.chatapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameField;
    EditText emailField;
    EditText passwordField;
    Button   registerBtn;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = findViewById(R.id.username_reg);
        emailField    = findViewById(R.id.email_reg);
        passwordField = findViewById(R.id.password);
        registerBtn   = findViewById(R.id.register_btn);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String email    = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password)
            ) {
                Toast.makeText(RegisterActivity.this, R.string.error_all_fields_required, Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, R.string.error_password_length, Toast.LENGTH_SHORT).show();
            } else {
                Register(username, email, password);
            }
        });
    }

    private void Register(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;

                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("search", username.toLowerCase());

                        reference.setValue(hashMap).addOnCompleteListener(userCreationTask -> {
                            if (userCreationTask.isSuccessful()) {
                                // TODO(fix): after registering you are not moved to main activity
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, R.string.error_cannot_register, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}