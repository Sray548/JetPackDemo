package com.example.jetpackdemo.ui.setting;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.jetpackdemo.beans.DeviceInfo;
import com.example.jetpackdemo.connect.DeviceConn;
import com.google.gson.Gson;

import java.nio.ByteBuffer;

public class SettingViewModel extends AndroidViewModel implements LifecycleObserver {

    private final MutableLiveData<String> mVersion;
    private DeviceConn mDeviceConn;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        mVersion = new MutableLiveData<>();
        mVersion.setValue("");
        mDeviceConn = DeviceConn.getInstance(application);
        mDeviceConn.setListener(new DeviceConn.DeviceConnListener() {
            @Override
            public int onDeviceConnect() {
                mDeviceConn.getDeviceInfo();
                return 0;
            }

            @Override
            public int onDeviceDisconnect() {
                return 0;
            }

            @Override
            public int onDeviceMessage(String msg, ByteBuffer data) {
                Gson gson = new Gson();
                DeviceInfo deviceInfo = gson.fromJson(msg, DeviceInfo.class);
                if (deviceInfo == null) return 0;
                mVersion.postValue(deviceInfo.getDevinfo().getVersion());
                return 0;
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        mDeviceConn.getDeviceInfo();
    }

    public MutableLiveData<String> getVersion() {
        return mVersion;
    }
}