package edu.msu.cse476.burkistr.cse476project;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvAlreadyAccount;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use signup.xml for the sign-up layout
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);

        btnSignUp.setOnClickListener(v -> {
            Log.d(TAG, "Sign Up button clicked");
            signUpUser();
        });

        tvAlreadyAccount.setOnClickListener(v -> {
            Log.d(TAG, "Navigating back to LoginActivity");
            finish();
        });
    }

    private void signUpUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        Log.d(TAG, "SignUp function called");

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Empty fields detected");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password too short");
            return;
        }

        Log.d(TAG, "Attempting to sign up user: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success; the user is automatically signed in.
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "createUserWithEmail:success - " + (user != null ? user.getEmail() : "null"));
                            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, TabbedActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign up fails, display a message to the user.
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Sign up failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
