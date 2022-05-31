package com.example.gpsspeedometerprox.serive;


import static com.example.gpsspeedometerprox.app.MyAppication.CHANNEL_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.example.gpsspeedometerprox.R;
import com.example.gpsspeedometerprox.model.activity.HomeActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LocationService extends Service {
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    double speed, speedMax, avgSpeed;
    LocationManager locationManager;
    public MyLocationListener listener;
    int seconds;
    String time;
    public boolean running;
    Handler handler;
    Runnable runnable;
    private ArrayList<Location> listLocations;

    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public LocationService getLocationService() {
            return LocationService.this;
        }
    }

    // create service
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("prox", "my service onCreate");
        running = true;

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("prox", "on bind serive");
        startLocation();
        getClock();
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("prox", "un bind serive");
        stopLocation();
        return super.onUnbind(intent);
    }


    @SuppressLint("MissingPermission")
    public void startLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) listener);
        }

        listLocations = new ArrayList<>();
        listLocations.clear();

    }

    private void stopLocation() {
        mCurrentLocation = null;
        lStart = null;
        lEnd = null;
        distance = 0;
        seconds = 0;
        speedMax = 0;
        speed = 0;
        handler.removeCallbacks(runnable);

        HomeActivity.dataTime("00:00:00");
        HomeActivity.dataSpeed("0 km/h", "0 Km/h", "0 Km");
        // lưu thông tin vào mảng
    }

    public class MyLocationListener implements LocationListener, GpsStatus.Listener {

        public void onLocationChanged(final Location loc) {
            if (HomeActivity.ckStartService) {
                Log.i("prox", "Start Location changed");
                getSpeed(loc);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    @SuppressLint("MissingPermission")
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    int satsInView = 0;
                    int satsUsed = 0;
                    Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                    for (GpsSatellite sat : sats) {
                        satsInView++;
                        if (sat.usedInFix()) {
                            satsUsed++;
                        }
                    }
                    Log.e("prox", "gps: " + (satsUsed) + "/" + (satsInView) + "cc");

                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(getBaseContext(), "lỗi gps", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    break;
            }
        }
    }

    public void getSpeed(Location location) {
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else {
            lEnd = mCurrentLocation;
        }
        speed = location.getSpeed() * 3.6f;

        if (speed > speedMax) {
            speedMax = (float) speed;
        }
        getValues();
        getListLocation(location);
    }

    public void getValues() {
        distance = distance + (lStart.distanceTo(lEnd) / 1000.0);
        lStart = lEnd;

        Log.e("prox", "speed: " + new DecimalFormat("##.##").format(speed));
        Log.e("prox", "max speed: " + new DecimalFormat("##.##").format(speedMax));
        Log.e("prox", "distance: " + new DecimalFormat("##.####").format(distance) + " Km");


        String sp = new DecimalFormat("##.##").format(speed);
        String maxsp = new DecimalFormat("##.##").format(speedMax);
        String dis = new DecimalFormat("##.##").format(distance);
        setDataActivityAndNotification(sp, maxsp, dis);
    }

    public void setDataActivityAndNotification(String speed, String speedMax, String distance) {
        HomeActivity.dataSpeed(speed, speedMax, distance);

        // ẩn notification khi uset setting ẩn
        sendNotifacationCustom(speed, distance, speedMax);

    }


    // show vị trí
    public void getListLocation(Location location) {
        Log.e("prox", "location: \n" + location.getLatitude() + "\n" + location.getLongitude());
        listLocations.add(location);
        getAddressString(location.getLatitude(), location.getLongitude());
    }

    private String getAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("address", strReturnedAddress.toString());
            } else {
                Log.e("address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("address", "Canont get Address!");
        }
        return strAdd;

    }

    public void getClock() {
        handler = new Handler();
        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                if (HomeActivity.ckLocation) {
                    seconds++;
                    time = String.format(Locale.getDefault(),
                            "%02d:%02d:%02d", hours,
                            minutes, secs);
                    HomeActivity.dataTime(time);
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    public void formatDistance(double dis) {
        double chuc = dis / 100;
        double k = dis / 1000;
        String strDis = String.format(Locale.getDefault(),
                "%02f:%02f", k, chuc);
        Log.e("prox", "dis: " + strDis + " Km/h");
    }

    // ------------------------------------------------------------------------
    //get data intent
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("prox", "forground serive");

        // get data => put len naviagation
        String speed = intent.getStringExtra("speed");
        String distance = intent.getStringExtra("distance");
        String max_speed = intent.getStringExtra("max_speed");

//        sendNotifacation(speed);
        sendNotifacationCustom(speed, distance, max_speed);

        // get data broadcast serive
        int actionClose = intent.getIntExtra("action_close_serive", 0);
        handleActionClose(actionClose);

        return START_NOT_STICKY;
    }

    private void sendNotifacationCustom(String speed, String distance, String max_speed) {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set Data notification
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_custom_notification);
        remoteViews.setTextViewText(R.id.tv_distance, distance);
        remoteViews.setTextViewText(R.id.tv_speed, speed);
        remoteViews.setTextViewText(R.id.tv_max_speed, max_speed);

        remoteViews.setOnClickPendingIntent(R.id.img_close, getPendingIntent(this, 1));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon_map)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .build();
        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_close", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void handleActionClose(int action) {
        if (action == 1) {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("prox", "my service onDestroy");

    }

    // -------------------------------------------------------------------------
    // check xem đã cấp quyền gps chưa nếu chưa thì hiện thị dialog xin cấp quyền
    public void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
            Toast.makeText(getBaseContext(), "GPS not", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
