package edu.msu.cse476.burkistr.cse476project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllAssignmentsFragment();
            case 1:
                return new CalendarFragment();
            case 2:
                return new AssignmentFragment();
            case 3:
                return new SettingsFragment();
            default:
                return new AllAssignmentsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
