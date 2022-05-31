package com.example.gpsspeedometerprox.baseActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gpsspeedometerprox.R;
import com.example.gpsspeedometerprox.model.activity.HomeActivity;
import com.example.gpsspeedometerprox.viewmodel.HomeViewModel;

public abstract class BaseFragment extends Fragment {

    private HomeViewModel homeViewModel;
    HomeActivity homeActivity;
    TextView tvAvgSpeed;
    TextView tvMaxSpeed;
    TextView tvDuration;
    TextView tvDiagital;
    TextView tvSatellite;
    Button btStartTrip;
    Button btResume, btStop;
    boolean checkResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        homeActivity = (HomeActivity) getActivity();
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        initView(view);
        initData();
        initEvent();

        return view;
    }


    public void initView(View view) {
        tvAvgSpeed = view.findViewById(R.id.tv_avg_speed);
        tvMaxSpeed = view.findViewById(R.id.tv_max_speed);
        tvDiagital = view.findViewById(R.id.tv_digital);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvSatellite = view.findViewById(R.id.tv_globe);
        btStartTrip = view.findViewById(R.id.bt_start_trip);
        btResume = view.findViewById(R.id.bt_resume);
        btStop = view.findViewById(R.id.bt_stop);

        btStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.startServic();
                homeViewModel.setCheckRuningService(true);
                homeViewModel.setCheckRuningLocation(true);
            }
        });
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.stopService();
                homeViewModel.setCheckRuningService(false);
                homeViewModel.setCheckRuningLocation(false);
            }
        });
        homeViewModel.getCheckRuningLocation().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                checkResume = aBoolean;
                if (aBoolean) {
                    btResume.setText(R.string.tv_pasu);
                } else {
                    btResume.setText(R.string.tv_resume);
                }
            }
        });
        btResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setCheckRuningLocation(!checkResume);
            }
        });
        homeViewModel.getCheckRuningService().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    btStartTrip.setVisibility(View.GONE);
                    btResume.setVisibility(View.VISIBLE);
                    btStop.setVisibility(View.VISIBLE);
                } else {
                    btStartTrip.setVisibility(View.VISIBLE);
                    btResume.setVisibility(View.GONE);
                    btStop.setVisibility(View.GONE);
                }
            }
        });

        homeViewModel.getDuration().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvDuration.setText(s);
            }
        });
        homeViewModel.getSpeed().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvAvgSpeed.setText(s);
            }
        });
        homeViewModel.getMaxSpeed().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvMaxSpeed.setText(s);
            }
        });
        homeViewModel.getDiagital().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvDiagital.setText(s);
            }
        });

    }

    protected abstract void initData();

    protected abstract void initEvent();

    protected abstract int getLayoutId();
}
