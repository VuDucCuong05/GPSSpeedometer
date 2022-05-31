package com.example.gpsspeedometerprox.model.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.gpsspeedometerprox.R;
import com.example.gpsspeedometerprox.baseActivity.BaseFragment;


public class AnalogFragment extends BaseFragment {


    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_analog;
    }
}