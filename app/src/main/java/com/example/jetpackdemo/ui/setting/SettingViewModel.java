package com.example.jetpackdemo.ui.setting;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.jetpackdemo.beans.DeviceInfo;
import com.example.jetpackdemo.connect.DeviceConn;
import com.example.jetpackdemo.connect.DeviceWebSocketClient;
import com.google.gson.Gson;

import java.nio.ByteBuffer;

public class SettingViewModel extends AndroidViewModel {

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

    public MutableLiveData<String> getVersion() {
        return mVersion;
    }
}