package edu.msu.cse476.burkistr.cse476project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class AllAssignmentsFragment extends Fragment {
    private static final String TAG = "AllAssignmentsFragment";
    private RecyclerView recyclerViewAssignments;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList;
    private FirebaseFirestore db;
    private String userId;

    public AllAssignmentsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_assignments, container, false);

        recyclerViewAssignments = root.findViewById(R.id.recyclerViewAssignments);
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(getContext(), assignmentList);
        recyclerViewAssignments.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firestore: order by year, then month, then day, then assignmentType
        Query query = db.collection("users")
                .document(userId)
                .collection("assignments")
                .orderBy("year")
                .orderBy("month")
                .orderBy("day")
                .orderBy("assignmentType");

        // Attach a snapshot listener to receive real-time updates.
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error fetching assignments", e);
                return;
            }
            assignmentList.clear();
            if (snapshots != null) {
                for (DocumentSnapshot document : snapshots.getDocuments()) {
                    Assignment assignment = document.toObject(Assignment.class);
                    if (assignment != null) {
                        assignment.setAssignmentId(document.getId());
                        assignmentList.add(assignment);
                    }
                }
            }
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Total assignments: " + assignmentList.size());
        });

        return root;
    }
}
