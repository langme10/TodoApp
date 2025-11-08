package edu.msu.cse476.burkistr.cse476project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogActivity extends AppCompatActivity {

    private Button btnSignOut;
    private Button btnGoToTabbed;
    private static final String TAG = "LogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log); // Make sure this XML has both sign-out and go-to-tabbed buttons

        btnSignOut = findViewById(R.id.btnSignOut);
        btnGoToTabbed = findViewById(R.id.btnGoToTabbed);

        // Sign out logic
        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Log.d(TAG, "User signed out");

            // Return to login
            Intent intent = new Intent(LogActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // "Go to Tabbed Activity"
        btnGoToTabbed.setOnClickListener(v -> {
            // Double-check your spelling: TabbedActivity.class
            Intent intent = new Intent(LogActivity.this, TabbedActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If no user is signed in, go back to login
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
