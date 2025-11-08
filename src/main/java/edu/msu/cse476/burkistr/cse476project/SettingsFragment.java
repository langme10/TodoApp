package edu.msu.cse476.burkistr.cse476project;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnSignOutSettings = root.findViewById(R.id.btnSignOutInSettings);
        btnSignOutSettings.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();


            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


            requireActivity().finish();
        });

        return root;
    }
}
