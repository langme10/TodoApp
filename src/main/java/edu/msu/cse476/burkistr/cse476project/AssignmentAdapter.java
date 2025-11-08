package edu.msu.cse476.burkistr.cse476project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList;
    private final Context context;

    public AssignmentAdapter(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        // Set assignment details
        holder.tvAssignmentName.setText(assignment.getAssignmentName());
        holder.tvClassName.setText(assignment.getClassName());

        String dueDateText = "Due: " + (assignment.getMonth() + 1) + "/" + assignment.getDay() + "/" + assignment.getYear();
        holder.tvDueDate.setText(dueDateText);

        // Apply the color to the card
        String colorName = assignment.getColor();
        applyColorToCard(holder.cardAssignment, colorName);

        // Checkbox logic
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(assignment.isCompleted());
        updateCardAppearance(holder, assignment.isCompleted());

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            assignment.setCompleted(isChecked);
            updateCardAppearance(holder, isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentDetailActivity.class);
            intent.putExtra("assignment", assignment);
            intent.putExtra("assignmentId", assignment.getAssignmentId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public void updateData(List<Assignment> newAssignments) {
        assignmentList = newAssignments;
        notifyDataSetChanged();
    }

    private void updateCardAppearance(@NonNull AssignmentViewHolder holder, boolean isCompleted) {
        if (isCompleted) {

            holder.cardAssignment.setAlpha(0.5f);
        } else {
            holder.cardAssignment.setAlpha(1.0f);
        }
    }

    private void applyColorToCard(CardView card, String colorName) {
        if (colorName == null || colorName.trim().isEmpty() || colorName.trim().equalsIgnoreCase("Choose Color")) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.defaultColor));
            return;
        }

        String color = colorName.trim();
        int colorResource;
        switch (color) {
            case "Red":
                colorResource = R.color.pastel_red;
                break;
            case "Pink":
                colorResource = R.color.pastel_pink;
                break;
            case "Blue":
                colorResource = R.color.pastel_blue;
                break;
            case "Green":
                colorResource = R.color.pastel_green;
                break;
            case "Yellow":
                colorResource = R.color.pastel_yellow;
                break;
            case "Purple":
                colorResource = R.color.pastel_purple;
                break;
            default:
                colorResource = R.color.defaultColor;
                break;
        }

        card.setCardBackgroundColor(ContextCompat.getColor(context, colorResource));
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {

        CardView cardAssignment;
        TextView tvAssignmentName;
        TextView tvClassName;
        TextView tvDueDate;
        CheckBox checkBoxCompleted;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAssignment = itemView.findViewById(R.id.cardAssignment);
            tvAssignmentName = itemView.findViewById(R.id.tvAssignmentName);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }
}
