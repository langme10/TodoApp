package edu.msu.cse476.burkistr.cse476project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.widget.AutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.widget.Filter;

public class AssignmentDetailActivity extends AppCompatActivity {
    private static final String TAG = "AssignmentDetailActivity";

    private EditText etName, etClass, etLocation;
    private AutoCompleteTextView etType;
    private AutoCompleteTextView spinnerAssignmentColor;
    private EditText etYear, etMonth, etDay;
    private Button btnEdit, btnSave, btnDelete, btnBack, btnPickDate;
    private TextView tvSelectedDate;

    private Assignment assignment;
    private String assignmentId;

    private FirebaseFirestore db;
    private String userId;
    private Calendar calendar;

    private LocationManager locationManager = null;
    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;
    private double toLatitude = 0;
    private double toLongitude = 0;

    private class ActiveListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
            registerListeners();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_detail);

        // Initialize views
        etName = findViewById(R.id.etName);
        etClass = findViewById(R.id.etClass);
        etLocation = findViewById(R.id.etLocation);
        etType = findViewById(R.id.etType);
        spinnerAssignmentColor = findViewById(R.id.spinnerAssignmentColor);
        etYear = findViewById(R.id.etYear);
        etMonth = findViewById(R.id.etMonth);
        etDay = findViewById(R.id.etDay);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        // Initialize calendar
        calendar = Calendar.getInstance();

        // Set up dropdowns
        setupDropdowns();

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        assignmentId = getIntent().getStringExtra("assignmentId");

        if (assignment != null) {
            populateFields();
            setFieldsEditable(false);
        } else {
            Toast.makeText(this, "Assignment data missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Date picker button listener
        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnEdit.setOnClickListener(v -> {
            setFieldsEditable(true);
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        });

        // Save button updates the assignment in Firestore
        btnSave.setOnClickListener(v -> saveAssignmentChanges());

        // Delete button removes the assignment
        btnDelete.setOnClickListener(v -> confirmDelete());

        // Back button returns to previous screen
        btnBack.setOnClickListener(v -> finish());

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        // Get the location manager
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        // Force the screen to say on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private ActiveListener activeListener = new ActiveListener();


    private void setupDropdowns() {
        // Assignment type dropdown options
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
                this, android.R.layout.simple_dropdown_item_1line, assignmentTypes) {
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = assignmentTypes;
                        results.count = assignmentTypes.length;
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        //  data set has changed
                        notifyDataSetChanged();
                    }

                    @Override
                    public CharSequence convertResultToString(Object resultValue) {
                        return resultValue.toString();
                    }
                };
            }
        };
        etType.setAdapter(typeAdapter);
        etType.setThreshold(0);
        etType.setOnClickListener(v -> {
            if (!etType.isPopupShowing()) {
                etType.showDropDown();
            }
        });

        // Assignment color dropdown options
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
                this, android.R.layout.simple_dropdown_item_1line, colorOptions) {
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = colorOptions;
                        results.count = colorOptions.length;
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public CharSequence convertResultToString(Object resultValue) {
                        return resultValue.toString();
                    }
                };
            }
        };
        spinnerAssignmentColor.setAdapter(colorAdapter);
        spinnerAssignmentColor.setThreshold(0);
        spinnerAssignmentColor.setOnClickListener(v -> {
            if (!spinnerAssignmentColor.isPopupShowing()) {
                spinnerAssignmentColor.showDropDown();
            }
        });
        spinnerAssignmentColor.setOnItemClickListener((parent, view, position, id) -> {
            String selectedColor = (String) parent.getItemAtPosition(position);
            spinnerAssignmentColor.setText(selectedColor, false);
        });
    }

    private void showDatePicker() {
        int year = assignment.getYear();
        int month = assignment.getMonth() - 1;
        int day = assignment.getDay();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);

                    etYear.setText(String.valueOf(selectedYear));
                    etMonth.setText(String.valueOf(selectedMonth + 1));
                    etDay.setText(String.valueOf(selectedDayOfMonth));

                    updateDateDisplay();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        tvSelectedDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void populateFields() {
        etName.setText(assignment.getAssignmentName());
        etClass.setText(assignment.getClassName());
        etLocation.setText(assignment.getLocation());
        etType.setText(assignment.getAssignmentType());

        if (assignment.getColor() != null && !assignment.getColor().isEmpty()) {
            spinnerAssignmentColor.setText(assignment.getColor());
        }

        etYear.setText(String.valueOf(assignment.getYear()));
        etMonth.setText(String.valueOf(assignment.getMonth()));
        etDay.setText(String.valueOf(assignment.getDay()));

        calendar.set(assignment.getYear(), assignment.getMonth() - 1, assignment.getDay());
        updateDateDisplay();
    }

    private void setFieldsEditable(boolean editable) {
        etName.setEnabled(editable);
        etClass.setEnabled(editable);
        etLocation.setEnabled(editable);
        etType.setEnabled(editable);
        spinnerAssignmentColor.setEnabled(editable);
        btnPickDate.setEnabled(editable);
    }

    private void saveAssignmentChanges() {
        String newName = etName.getText().toString().trim();
        String newClass = etClass.getText().toString().trim();
        String newLoc = etLocation.getText().toString().trim();
        String newType = etType.getText().toString().trim();
        String newColor = spinnerAssignmentColor.getText().toString().trim();

        if (newName.isEmpty() || newClass.isEmpty() || newType.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int newYear, newMonth, newDay;
        try {
            newYear = Integer.parseInt(etYear.getText().toString().trim());
            newMonth = Integer.parseInt(etMonth.getText().toString().trim());
            newDay = Integer.parseInt(etDay.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid date values", Toast.LENGTH_SHORT).show();
            return;
        }

        assignment.setAssignmentName(newName);
        assignment.setClassName(newClass);
        assignment.setLocation(newLoc);
        assignment.setAssignmentType(newType);
        assignment.setColor(newColor);
        assignment.setYear(newYear);
        assignment.setMonth(newMonth);
        assignment.setDay(newDay);

        db.collection("users").document(userId)
                .collection("assignments").document(assignmentId)
                .set(assignment)
                .addOnSuccessListener(aVoid -> {
                    showSuccessMessage();
                    Log.d(TAG, "Assignment updated successfully");
                    setFieldsEditable(false);
                    btnSave.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AssignmentDetailActivity.this, "Error updating assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating assignment", e);
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Assignment")
                .setMessage("Are you sure you want to delete this assignment?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAssignment())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSuccessMessage() {
        AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Changes saved successfully!")
                .setIcon(android.R.drawable.ic_dialog_info)
                .create();

        successDialog.show();

        new android.os.Handler().postDelayed(
                successDialog::dismiss,
                1500
        );
    }

    private void deleteAssignment() {
        db.collection("users").document(userId)
                .collection("assignments").document(assignmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AssignmentDetailActivity.this, "Assignment deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AssignmentDetailActivity.this, "Error deleting assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting assignment", e);
                });
    }

    /**
     * Called when this application becomes foreground again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerListeners();
    }

    /**
     * Called when this application is no longer the foreground application.
     */
    @Override
    protected void onPause() {
        unregisterListeners();
        super.onPause();
    }

    private void registerListeners() {
        unregisterListeners();

        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        String bestAvailable = locationManager.getBestProvider(criteria, true);

        if (bestAvailable != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(bestAvailable, 500, 1,
                    activeListener);
            Location location =
                    locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }

    }

    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }

    private void onLocation(Location location) {
        if (location == null) {
            return;
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;
    }

    // This method will be called when the directions button is clicked
    public void onShowDirections(View view) {
        if (!valid) {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String address = etLocation.getText().toString().trim();
        if (newAddress(address))
        {
            // Get selected travel mode
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int selectedId = radioGroup.getCheckedRadioButtonId();

            String travelMode;
            if (selectedId == R.id.radioButtonBicycle) { // Bicycle
                travelMode = "bicycling";
            } else if (selectedId == R.id.radioButtonWalk) { // Walk
                travelMode = "walking";
            } else if (selectedId == R.id.radioButtonCar) { // Car
                travelMode = "driving";
            } else {
                Toast.makeText(this, "Please select a travel mode", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create URI for Google Maps directions
            String uri = String.format(Locale.ENGLISH,
                    "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f&travelmode=%s",
                    latitude,
                    longitude,
                    toLatitude,
                    toLongitude,
                    Uri.encode(travelMode));

            // Create an intent to launch Google Maps
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            // Verify that Google Maps is installed
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // If Google Maps isn't installed, open in browser
                intent.setPackage(null);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(this, R.string.exceptionNoLocation,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle setting a new "to" location.
     *
     * @param lat     latitude
     * @param lon     longitude
     */
    private void newTo(double lat, double lon) {
        toLatitude = lat;
        toLongitude = lon;
    }

    private boolean newAddress(final String address) {
        if (address.equals("")) {
            // Don't do anything if the address is blank
            return false;
        }
        lookupAddress(address);
        return true;
    }

    /**
     * Look up the provided address.
     *
     * @param address Address we are looking up
     */
    private void lookupAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.US);
        boolean exception = false;
        List<Address> locations;
        try {
            locations = geocoder.getFromLocationName(address, 1);
        } catch (IOException ex) {
            // Failed due to I/O exception
            locations = null;
            exception = true;
        }

        final boolean finalException = exception;
        final List<Address> finalLocations = locations;
        final String finalAddress = address;

        newLocation(finalAddress, finalException, finalLocations);
    }

    private void newLocation(String address, boolean exception, List<Address>
            locations) {
        if (exception) {
            Toast.makeText(this, R.string.exception,
                    Toast.LENGTH_SHORT).show();
        } else {
            if (locations == null || locations.size() == 0) {
                Toast.makeText(this, R.string.couldnotfind,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            EditText location = (EditText) findViewById(R.id.etLocation);
            Address a = locations.get(0);
            newTo(a.getLatitude(), a.getLongitude());
        }
    }
}
