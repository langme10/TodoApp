package edu.msu.cse476.burkistr.cse476project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {
    private static final String TAG = "CalendarFragment";
    private CalendarView calendarView;
    private TextView tvDateDetails;
    private RecyclerView recyclerViewAssignments;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList;
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = root.findViewById(R.id.calendarView);
        tvDateDetails = root.findViewById(R.id.tvDateDetails);
        recyclerViewAssignments = root.findViewById(R.id.recyclerViewAssignments);

        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(getContext(), assignmentList);
        recyclerViewAssignments.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query assignments for the current date on startup.
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH); // Note: January = 0
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // Update the date details TextView
        String currentDateString = (currentMonth + 1) + "/" + currentDay + "/" + currentYear;
        tvDateDetails.setText("Assignments for: " + currentDateString);

        // Query assignments for the current day.
        queryAssignmentsForDate(currentYear, currentMonth, currentDay);

        // Set up listener for date changes on the CalendarView.
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
            tvDateDetails.setText("Assignments for: " + selectedDate);
            queryAssignmentsForDate(year, month, dayOfMonth);
        });

        return root;
    }

    private void queryAssignmentsForDate(int year, int month, int day) {
        Query query = db.collection("users")
                .document(userId)
                .collection("assignments")
                .whereEqualTo("year", year)
                .whereEqualTo("month", month)
                .whereEqualTo("day", day)
                .orderBy("assignmentType");

        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                return;
            }

            assignmentList.clear();
            if (snapshots != null) {
                for (DocumentSnapshot document : snapshots) {
                    Assignment assignment = document.toObject(Assignment.class);
                    if (assignment != null) {
                        assignment.setAssignmentId(document.getId());
                        assignmentList.add(assignment);
                    }
                }
            }
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Updated assignments: " + assignmentList.size());
        });
    }
}
