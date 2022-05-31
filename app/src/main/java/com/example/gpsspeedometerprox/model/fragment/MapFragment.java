package com.example.gpsspeedometerprox.model.fragment;

import android.view.View;
import android.widget.Toast;

import com.example.gpsspeedometerprox.R;
import com.example.gpsspeedometerprox.baseActivity.BaseFragment;


public class MapFragment extends BaseFragment {


    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        Toast.makeText(getContext(), "prox", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }
}