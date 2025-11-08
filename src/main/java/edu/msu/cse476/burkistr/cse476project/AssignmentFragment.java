package edu.msu.cse476.burkistr.cse476project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AssignmentFragment extends Fragment {
    private static final String TAG = "AssignmentFragment";

    private EditText etAssignmentName, etAssignmentClass, etAssignmentLoc;
    private AutoCompleteTextView spinnerAssignmentType, spinnerAssignmentColor;
    private TextView tvSelectedDate;
    private Button btnPickDate, btnAddAssignment;

    private int selectedYear = -1;
    private int selectedMonth = -1;
    private int selectedDay = -1;

    public AssignmentFragment() {

    }


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_assignment, container, false);

        etAssignmentName = root.findViewById(R.id.etAssignmentName);
        etAssignmentClass = root.findViewById(R.id.etAssignmentClass);
        etAssignmentLoc = root.findViewById(R.id.etAssignmentLocation);
        spinnerAssignmentType = root.findViewById(R.id.spinnerAssignmentType);
        spinnerAssignmentColor = root.findViewById(R.id.spinnerAssignmentColor);
        tvSelectedDate = root.findViewById(R.id.tvSelectedDate);
        btnPickDate = root.findViewById(R.id.btnPickDate);
        btnAddAssignment = root.findViewById(R.id.btnAddAssignment);

        setupTypeSpinner();
        setupColorSpinner();

        // Button to pick the due date
        btnPickDate.setOnClickListener(v -> showDatePickerDialog());

        // Button to add the assignment
        btnAddAssignment.setOnClickListener(v -> addAssignment());

        return root;
    }

    private void setupTypeSpinner() {
        String[] assignmentTypes = {
                "Choose Type",
                "Exam",
                "Homework",
                "Reading",
                "Project",
                "Quiz",
                "Misc."
        };

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                assignmentTypes
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignmentType.setAdapter(typeAdapter);
    }

    private void setupColorSpinner() {
        String[] colorOptions = {
                "Choose Color",
                "Red",
                "Pink",
                "Blue",
                "Green",
                "Yellow",
                "Purple"
        };

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                colorOptions
        );

        spinnerAssignmentColor.setAdapter(colorAdapter);

        // Set default selection
        spinnerAssignmentColor.setText("Choose Color", false);

        spinnerAssignmentColor.setOnItemClickListener((parent, view, position, id) -> {
            String selectedColor = (String) parent.getItemAtPosition(position);
            spinnerAssignmentColor.setText(selectedColor, false);
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(
                requireContext(),
                (DatePicker datePicker, int chosenYear, int chosenMonth, int chosenDay) -> {
                    selectedYear = chosenYear;
                    selectedMonth = chosenMonth;
                    selectedDay = chosenDay;

                    String dateText = (selectedMonth + 1) + "/" + chosenDay + "/" + chosenYear;
                    tvSelectedDate.setText(dateText);
                },
                year, month, day
        );
        dpd.show();
    }
    private int getColorResource(String colorName) {
        switch (colorName) {
            case "Red":
                return R.color.pastel_red;
            case "Pink":
                return R.color.pastel_pink;
            case "Blue":
                return R.color.pastel_blue;
            case "Green":
                return R.color.pastel_green;
            case "Yellow":
                return R.color.pastel_yellow;
            case "Purple":
                return R.color.pastel_purple;
            default:
                return R.color.defaultColor;
        }
    }

    private void addAssignment() {
        String name = etAssignmentName.getText().toString().trim();
        String cls = etAssignmentClass.getText().toString().trim();
        String loc = etAssignmentLoc.getText().toString().trim();
        String type = spinnerAssignmentType.getText().toString();
        String chosenColor = spinnerAssignmentColor.getText().toString();

        // Validate fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(cls)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Choose Type".equals(type)) {
            Toast.makeText(getContext(), "Please select an assignment type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedYear == -1) {
            Toast.makeText(getContext(), "Please pick a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the assignment with the selected color
        Assignment assignment = new Assignment(
                name,
                cls,
                loc,
                type,
                selectedYear,
                selectedMonth,
                selectedDay,
                chosenColor
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("assignments")
                .add(assignment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Assignment added!", Toast.LENGTH_SHORT).show();

                    // Reset form fields
                    etAssignmentName.setText("");
                    etAssignmentClass.setText("");
                    etAssignmentLoc.setText("");
                    spinnerAssignmentType.setText("");
                    spinnerAssignmentColor.setText("Choose Color", false);
                    selectedYear = selectedMonth = selectedDay = -1;
                    tvSelectedDate.setText("No date selected");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error adding assignment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error adding assignment to Firestore", e);
                });
    }
}
