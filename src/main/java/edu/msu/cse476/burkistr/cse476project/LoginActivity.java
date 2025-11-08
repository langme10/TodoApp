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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class LoginActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView tvAlreadyAccount;
    private Button btnLogin;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        editTextEmail = findViewById(R.id.etEmail);
        editTextPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);

        // Set up click listeners
        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            loginUser();
        });

        tvAlreadyAccount.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to SignUpActivity");
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }


    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        Log.d(TAG, "Login function");

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Email and password cannot be empty", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Empty email or password on Login");
            return;
        }

        Log.d(TAG, "Attempting to log in user: " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, TabbedActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
