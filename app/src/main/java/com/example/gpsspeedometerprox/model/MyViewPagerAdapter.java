package com.example.gpsspeedometerprox.model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gpsspeedometerprox.model.fragment.AnalogFragment;
import com.example.gpsspeedometerprox.model.fragment.DigitalFragment;
import com.example.gpsspeedometerprox.model.fragment.MapFragment;


public class MyViewPagerAdapter extends FragmentStateAdapter {


    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AnalogFragment();
            case 1:
                return new DigitalFragment();
            default:
                return new MapFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
