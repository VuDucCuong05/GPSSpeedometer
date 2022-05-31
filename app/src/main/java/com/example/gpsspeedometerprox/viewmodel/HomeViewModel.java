package com.example.gpsspeedometerprox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<String> speed;
    private MutableLiveData<String> maxSpeed;
    private MutableLiveData<String> diagital;
    private MutableLiveData<String> duration;
    private MutableLiveData<Boolean> checkRuningService;
    private MutableLiveData<Boolean> checkRuningLocation;
    private MutableLiveData<Boolean> checkRotateScreen;
    private MutableLiveData<Boolean> checkSetting;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        speed = new MutableLiveData<>();
        maxSpeed = new MutableLiveData<>();
        diagital = new MutableLiveData<>();
        duration = new MutableLiveData<>();
        checkRuningService = new MutableLiveData<>();
        checkRuningLocation = new MutableLiveData<>();
        checkRotateScreen = new MutableLiveData<>();
        checkSetting = new MutableLiveData<>();
    }

    public MutableLiveData<String> getSpeed() {
        return speed;
    }

    public void setSpeed(String s) {
       speed.setValue(s);
    }

    public MutableLiveData<String> getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String s) {
        maxSpeed.setValue(s);
    }

    public MutableLiveData<String> getDiagital() {
        return diagital;
    }

    public void setDiagital(String s) {
        diagital.postValue(s);
    }

    public MutableLiveData<String> getDuration() {
        return duration;
    }

    public void setDuration(String s) {
        duration.postValue(s);
    }

    public MutableLiveData<Boolean> getCheckRuningService() {
        return checkRuningService;
    }

    public void setCheckRuningService(boolean ck) {
       checkRuningService.setValue(ck);
    }
    public MutableLiveData<Boolean> getCheckRuningLocation() {
        return checkRuningLocation;
    }

    public void setCheckRuningLocation(boolean ck) {
        checkRuningLocation.setValue(ck);
    }
    public MutableLiveData<Boolean> getCheckRotateScreen() {
        return checkRotateScreen;
    }

    public void setCheckRotateScreen(boolean ck) {
        checkRotateScreen.setValue(ck);
    }
    public MutableLiveData<Boolean> getCheckSetting() {
        return checkSetting;
    }

    public void setCheckSetting(boolean ck) {
        checkSetting.setValue(ck);
    }
}
