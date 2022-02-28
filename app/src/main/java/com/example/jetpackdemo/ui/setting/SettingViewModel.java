package com.example.jetpackdemo.ui.setting;

import static android.content.Context.MODE_PRIVATE;

import static com.example.jetpackdemo.Constant.UNIT;
import static com.example.jetpackdemo.Constant.UNIT_TYPE;
import static com.example.jetpackdemo.ui.setting.SettingFragment.SetMode.SYS_LANG;
import static com.example.jetpackdemo.util.UnitUtils.IMPERIAL;
import static com.example.jetpackdemo.util.UnitUtils.METRIC;
import static com.example.jetpackdemo.util.UnitUtils.US;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.jetpackdemo.Constant;
import com.example.jetpackdemo.R;
import com.example.jetpackdemo.beans.DeviceInfo;
import com.example.jetpackdemo.connect.DeviceConn;
import com.example.jetpackdemo.util.log.Logger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingViewModel extends AndroidViewModel implements LifecycleObserver {
    private Logger mLog = Logger.create("SettingViewModel");

    private final MutableLiveData<String> mVersion;
    private final MutableLiveData<String> mSysLang;
    private final MutableLiveData<String> mUnit;
    private final MutableLiveData<String> mWiFiSSid;
    private final MutableLiveData<String> mWiFiMode;
    private final MutableLiveData<Integer> mRet;
    private final MutableLiveData<Integer> mPosition;
    private final MutableLiveData<Integer> mTitle;
    private final MutableLiveData<Integer> mMsg;
    private final MutableLiveData<List<String>> mDatas;
    private final MutableLiveData<SettingFragment.SetMode> mMode;
    private SharedPreferences mSp;

    private String mPsd;

    public static final String mInitPSD = "********";

    private ArrayList<String> datas = new ArrayList<>();

    private DeviceConn mDeviceConn;
    private Application mApplication;
    private DeviceInfo mDeviceInfo;


    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.mApplication = application;
        mVersion = new MutableLiveData<>();
        mSysLang = new MutableLiveData<>();
        mUnit = new MutableLiveData<>();
        mDatas = new MutableLiveData<>();
        mRet = new MutableLiveData<>();
        mPosition = new MutableLiveData<>();
        mTitle = new MutableLiveData<>();
        mMsg = new MutableLiveData<>();
        mMode = new MutableLiveData<>();
        mWiFiSSid = new MutableLiveData<>();
        mWiFiMode = new MutableLiveData<>();

        mDatas.setValue(new ArrayList<>());
        mVersion.setValue("");
        mSysLang.setValue("");
        mUnit.setValue("");
        mWiFiSSid.setValue("");
        mWiFiMode.setValue("");
        mRet.setValue(0);
        mPosition.setValue(0);
        mPosition.setValue(0);
        mMode.setValue(SYS_LANG);
        mMsg.setValue(0);
        mTitle.setValue(0);

        mDeviceConn = DeviceConn.getInstance(application);
        mDeviceConn.setListener(mListener);

        resetUnit();
    }

    private DeviceConn.DeviceConnListener mListener = new DeviceConn.DeviceConnListener() {
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
            try {
                JSONObject jso = new JSONObject(msg);
                if (jso.optString("cmd").equals(DeviceConn.SETDEVICEINFO_COMMAND)) {
                    mDeviceConn.getDeviceInfo();
                    mRet.postValue(jso.optInt("ret"));
                    Toast.makeText(mApplication, R.string.modify_success, Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new Gson();
                    mDeviceInfo = gson.fromJson(msg, DeviceInfo.class);
                    if (mDeviceInfo == null) return 0;
                    mVersion.postValue(mDeviceInfo.getDevinfo().getVersion());
                    resetSysLang();
                    mWiFiSSid.postValue(mDeviceInfo.getWifi().getSsid());
                    mWiFiMode.postValue(mDeviceInfo.getWifi().is_$5GMode() ? "5G" : "2.4G");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    private void resetPosition() {
        switch (Objects.requireNonNull(mMode.getValue())) {
            case SYS_LANG:
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mSysLang.getValue()));
                break;
            case UNIT:
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mUnit.getValue()));
                break;
            case WIFI_MODE:
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mWiFiMode.getValue()));
                break;
        }
    }

    private void resetSysLang() {
        String language = mDeviceInfo.getLanguage();
        if (TextUtils.isEmpty(language) || Constant.EN_US.equals(language)) {
            mSysLang.postValue(mApplication.getResources().getString(R.string.en_us));
        } else if (Constant.ZH_CN.equals(language)) {
            mSysLang.postValue(mApplication.getResources().getString(R.string.zh_cn));
        } else if (Constant.ZH_TW.equals(language)) {
            mSysLang.postValue(mApplication.getResources().getString(R.string.zh_tw));
        } else if (Constant.JA_JP.equals(language)) {
            mSysLang.postValue(mApplication.getResources().getString(R.string.ja_jp));
        }
    }

    private void resetUnit() {
        if (mSp == null) {
            mSp = mApplication.getSharedPreferences(UNIT_TYPE, MODE_PRIVATE);
        }
        switch (mSp.getInt(UNIT, METRIC)) {
            case METRIC:
                mUnit.setValue(mApplication.getResources().getString(R.string.metric));
                break;
            case IMPERIAL:
                mUnit.setValue(mApplication.getResources().getString(R.string.british));
                break;
            case US:
                mUnit.setValue(mApplication.getResources().getString(R.string.us));
                break;
        }
    }

    private void resetWiFi() {
        mWiFiSSid.setValue(mDeviceInfo.getWifi().getSsid());
    }

    private void resetWiFiMode() {
        mWiFiMode.setValue(mDeviceInfo.getWifi().is_$5GMode() ? "5G" : "2.4G");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        mDeviceConn.getDeviceInfo();
    }

    public DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }

    public MutableLiveData<String> getVersion() {
        return mVersion;
    }

    public MutableLiveData<String> getUnit() {
        return mUnit;
    }

    public MutableLiveData<String> getWiFi() {
        return mWiFiSSid;
    }

    public MutableLiveData<String> getWiFiMode() {
        return mWiFiMode;
    }

    public MutableLiveData<String> getSysLang() {
        return mSysLang;
    }

    public MutableLiveData<Integer> getRet() {
        return mRet;
    }

    public MutableLiveData<Integer> getPosition() {
        return mPosition;
    }

    public MutableLiveData<Integer> getTitle() {
        return mTitle;
    }

    public MutableLiveData<Integer> getMsg() {
        return mMsg;
    }

    public void initData() {
        datas.clear();
        switch (Objects.requireNonNull(mMode.getValue())) {
            case SYS_LANG:
                datas.add(mApplication.getResources().getString(R.string.en_us));
                datas.add(mApplication.getResources().getString(R.string.zh_cn));
                datas.add(mApplication.getResources().getString(R.string.zh_tw));
                datas.add(mApplication.getResources().getString(R.string.ja_jp));
                break;
            case UNIT:
                datas.add(mApplication.getResources().getString(R.string.metric));
                datas.add(mApplication.getResources().getString(R.string.british));
                datas.add(mApplication.getResources().getString(R.string.us));
                break;
            case WIFI_MODE:
                datas.add("5G");
                datas.add("2.4G");
                break;
        }
        mDatas.setValue(datas);
    }

    public void initTitleMsg() {
        switch (Objects.requireNonNull(mMode.getValue())) {
            case SYS_LANG:
                mTitle.setValue(R.string.sys_lang);
                break;
            case UNIT:
                mTitle.setValue(R.string.unit);
                break;
            case WIFI:
                mTitle.setValue(R.string.wifi_info);
                break;
            case WIFI_MODE:
                mTitle.setValue(R.string.wifi_mode);
                mMsg.setValue(R.string.wifi_mode_tip);
                break;
        }
    }

    public MutableLiveData<List<String>> getDatas() {
        return mDatas;
    }

    public void select(int position) {
        switch (Objects.requireNonNull(mMode.getValue())) {
            case SYS_LANG:
                mSysLang.setValue(Objects.requireNonNull(mDatas.getValue()).get(position));
                break;
            case UNIT:
                mUnit.setValue(Objects.requireNonNull(mDatas.getValue()).get(position));
                break;
            case WIFI_MODE:
                mWiFiMode.setValue(Objects.requireNonNull(mDatas.getValue()).get(position));
        }
        mPosition.setValue(position);
    }

    public void ok() {
        switch (Objects.requireNonNull(mMode.getValue())) {
            case SYS_LANG:
                String lang = "";
                switch (Objects.requireNonNull(mDatas.getValue()).indexOf(mSysLang.getValue())) {
                    case 0:
                        lang = Constant.EN_US;
                        break;
                    case 1:
                        lang = Constant.ZH_CN;
                        break;
                    case 2:
                        lang = Constant.ZH_TW;
                        break;
                    case 3:
                        lang = Constant.JA_JP;
                        break;
                }
                mDeviceConn.setLanguage(lang);
                break;
            case UNIT:
                SharedPreferences.Editor edit = mSp.edit();
                edit.putInt(UNIT, Objects.requireNonNull(mDatas.getValue()).indexOf(mUnit.getValue()));
                edit.apply();
                break;
            case WIFI:
                mDeviceConn.setWiFiInfo(mWiFiSSid.getValue(), mPsd);
                break;
            case WIFI_MODE:
                mDeviceConn.setWiFiMode(Objects.requireNonNull(mPosition.getValue()));
                break;
        }
    }

    public void cancel() {
        switch (Objects.requireNonNull(mMode.getValue())) {
            case SYS_LANG:
                resetSysLang();
                break;
            case UNIT:
                resetUnit();
                break;
            case WIFI:
                resetWiFi();
                break;
            case WIFI_MODE:
                resetWiFiMode();
                break;
        }
    }

    public void mode(SettingFragment.SetMode mode) {
        mMode.setValue(mode);
        initTitleMsg();
        initData();
        resetPosition();
    }

    public MutableLiveData<SettingFragment.SetMode> getMode() {
        return mMode;
    }

    public void setWiFiInfo(String ssid, String psd) {
        if (TextUtils.isEmpty(ssid)) {
            Toast.makeText(mApplication, R.string.wifi_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ssid.length() > 16) {
            Toast.makeText(mApplication, R.string.wifi_name_too_long, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(mApplication, R.string.psd_too_short, Toast.LENGTH_SHORT).show();
            return;
        }
        if (psd.length() < 8) {
            Toast.makeText(mApplication, R.string.psd_too_short, Toast.LENGTH_SHORT).show();
            return;
        }
        if (psd.length() > 16) {
            Toast.makeText(mApplication, R.string.psd_too_long, Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager manager = (InputMethodManager) mApplication.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            if (psd.equals(mInitPSD)) {
                psd = "";
            }
            mWiFiSSid.setValue(ssid);
            this.mPsd = psd;
        }
    }
}