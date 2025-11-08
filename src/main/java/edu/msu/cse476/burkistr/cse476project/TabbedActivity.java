package edu.msu.cse476.burkistr.cse476project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class TabbedActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;

    public static List<Assignment> assignments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All Assignments");
                    break;
                case 1:
                    tab.setText("Calendar");
                    break;
                case 2:
                    tab.setText("Add Assignment");
                    break;
                case 3:
                    tab.setText("Settings");
                    break;
            }
        }).attach();

        int startingTab = getIntent().getIntExtra("TAB_INDEX", 0);
        viewPager.setCurrentItem(startingTab, false);
    }
}
