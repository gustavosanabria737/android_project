package com.eventoscercanos.osmdroid.nuevo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.eventoscercanos.osmdroid.R;
import com.eventoscercanos.osmdroid.nuevo.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class Iconos_fragment extends Fragment {

    private String origen_llamada, activity_origen;

    public Iconos_fragment() {
    } // Required empty public constructor

    public static Iconos_fragment newInstance(String activity_origen) {
        Iconos_fragment fragment = new Iconos_fragment();
        Bundle args = new Bundle();
        args.putString("activity_origen", activity_origen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            activity_origen = getArguments().getString("activity_origen");
        }
   }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_iconos, container, false);
        TextView title;
        title = v.findViewById(R.id.txt_title_markers);
        title.setText(R.string.fragment_iconos_title);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), getLifecycle());
        ViewPager2 viewPager = v.findViewById(R.id.viewPager2);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = v.findViewById(R.id.tabs);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setIcon(R.drawable.mk_pers_male);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.work_industry);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.veh_car);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.cons_home);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.restaurant_marker);
                        break;
                }

            }
        });
        tabLayoutMediator.attach();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}









