package edu.msu.cse476.burkistr.cse476project;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to the log page.
            startActivity(new Intent(LauncherActivity.this, TabbedActivity.class));
        } else {
            // No user is signed in, navigate to the sign in page.
            startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
        }
        // Finish LauncherActivity so the user can't return to it.
        finish();
    }
}
