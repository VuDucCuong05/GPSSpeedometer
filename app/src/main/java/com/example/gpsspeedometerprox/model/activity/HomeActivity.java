package com.example.gpsspeedometerprox.model.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gpsspeedometerprox.R;
import com.example.gpsspeedometerprox.baseActivity.BaseActivity;
import com.example.gpsspeedometerprox.model.MyViewPagerAdapter;
import com.example.gpsspeedometerprox.model.fragment.SettingFragment;
import com.example.gpsspeedometerprox.serive.LocationService;
import com.example.gpsspeedometerprox.viewmodel.HomeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends BaseActivity {
    FrameLayout layoutFragmentHome;
    TabLayout tabLayoutHome;
    ViewPager2 viewPagerHome2;
    MyViewPagerAdapter myViewPagerAdapter;
    ImageView imgSetting, imgTurmScreen;
    TextView tvGps, tvGlobe;
    static HomeViewModel homeViewModel;

    private LocationService mLocationService;
    private boolean isServiceConnected;
    public static boolean ckLocation;
    public static boolean ckStartService;
    boolean ckRotate;
    boolean ckSetting;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            LocationService.MyBinder myBinder = (LocationService.MyBinder) iBinder;
            mLocationService = myBinder.getLocationService();
            isServiceConnected = true;
            mLocationService.checkGps();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnected = false;
        }
    };

    @Override
    protected void initView() {

        tabLayoutHome = findViewById(R.id.tab_layout_home);
        viewPagerHome2 = findViewById(R.id.view_pager_home);

        myViewPagerAdapter = new MyViewPagerAdapter(HomeActivity.this);
        viewPagerHome2.setAdapter(myViewPagerAdapter);

        imgSetting = findViewById(R.id.img_setting);
        imgTurmScreen = findViewById(R.id.img_turm_screen);

        layoutFragmentHome = findViewById(R.id.layout_fragment_home);
        homeViewModel = new ViewModelProvider(HomeActivity.this).get(HomeViewModel.class);

    }


    @Override
    protected void initData() {
        // tabLayout
        new TabLayoutMediator(tabLayoutHome, viewPagerHome2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.tv_analog);
                        tab.setIcon(R.drawable.custom_analog);
                        break;
                    case 1:
                        tab.setText(R.string.tv_digital);
                        tab.setIcon(R.drawable.custom_digital);

                        break;
                    case 2:
                        tab.setText(R.string.tv_map);
                        tab.setIcon(R.drawable.custom_map);

                        break;
                    default:
                        tab.setText(R.string.tv_analog);
                        tab.setIcon(R.drawable.custom_analog);

                        break;
                }
            }
        }).attach();
    }

    @Override
    protected void initEvent() {


        homeViewModel.getCheckRotateScreen().observe(HomeActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ckRotate = aBoolean;
            }
        });

        imgTurmScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ckRotate) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    homeViewModel.setCheckRotateScreen(true);
                    homeViewModel.setCheckSetting(true);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    homeViewModel.setCheckRotateScreen(false);
                }
            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ckRotate) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    homeViewModel.setCheckRotateScreen(false);
                    homeViewModel.setCheckSetting(false);
                } else {
                    Toast.makeText(getBaseContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();

                }
            }
        });
        homeViewModel.getCheckSetting().observe(HomeActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    layoutFragmentHome.setVisibility(View.VISIBLE);
                    getFragment(new SettingFragment());
                } else {
//                    layoutFragmentHome.setVisibility(View.GONE);
                }
            }
        });


        homeViewModel.getCheckRuningService().observe(HomeActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ckStartService = aBoolean;
            }
        });

        homeViewModel.getCheckRuningLocation().observe(HomeActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ckLocation = aBoolean;
            }
        });

    }

    public static void dataTime(String time) {
        homeViewModel.setDuration(time);
    }

    public static void dataSpeed(String speed, String speedMax, String distance) {
        homeViewModel.setSpeed(speed);
        homeViewModel.setMaxSpeed(speedMax);
        homeViewModel.setDiagital(distance);
    }

    @Override
    protected int getLayoutId() {
//        getSupportActionBar().hide();
        showHideFullScreen();
        return R.layout.layout_home;
    }

    public void showHideFullScreen() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    public void startServic() {
        if (isServiceConnected == true)
            return;
        Intent intent = new Intent(HomeActivity.this, LocationService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    public void stopService() {
        Intent intent = new Intent(HomeActivity.this, LocationService.class);
        stopService(intent);
        stopBoundService();
    }


    private void stopBoundService() {
        if (isServiceConnected) {
            unbindService(serviceConnection);
            isServiceConnected = false;
        }
    }

    public void getFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment_home, fragment)
                .addToBackStack(null)
                .commit();
    }

    // khi backstack thi quay ve man hinh ngang




}