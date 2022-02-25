package com.example.jetpackdemo.ui.setting;

import static android.content.Context.MODE_PRIVATE;

import static com.example.jetpackdemo.Constant.UNIT;
import static com.example.jetpackdemo.Constant.UNIT_TYPE;
import static com.example.jetpackdemo.util.UnitUtils.IMPERIAL;
import static com.example.jetpackdemo.util.UnitUtils.METRIC;
import static com.example.jetpackdemo.util.UnitUtils.US;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
    private final MutableLiveData<Integer> mRet;
    private final MutableLiveData<Integer> mPosition;
    private final MutableLiveData<List<String>> mDatas;
    private SharedPreferences mSp;

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

        mDatas.setValue(new ArrayList<>());
        mVersion.setValue("");
        mSysLang.setValue("");
        mSysLang.setValue("");
        mRet.setValue(0);
        mPosition.setValue(0);

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
                } else {
                    Gson gson = new Gson();
                    mDeviceInfo = gson.fromJson(msg, DeviceInfo.class);
                    if (mDeviceInfo == null) return 0;
                    mVersion.postValue(mDeviceInfo.getDevinfo().getVersion());

                    resetSysLang();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    public void resetPosition(SettingFragment.SetMode setMode) {
        switch (setMode) {
            case SYS_LANG:
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mSysLang.getValue()));
                break;
            case UNIT:
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mUnit.getValue()));
                break;
        }
    }

    public void resetSysLang() {
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

    public void resetUnit() {
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

    public MutableLiveData<String> getSysLang() {
        return mSysLang;
    }

    public MutableLiveData<Integer> getRet() {
        return mRet;
    }

    public MutableLiveData<Integer> getPosition() {
        return mPosition;
    }

    public void initData(SettingFragment.SetMode setMode) {
        datas.clear();
        switch (setMode) {
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
        }
        mDatas.setValue(datas);
    }

    public MutableLiveData<List<String>> getDatas() {
        return mDatas;
    }

    public void select(int position, SettingFragment.SetMode setMode) {
        switch (setMode) {
            case SYS_LANG:
                mSysLang.setValue(Objects.requireNonNull(mDatas.getValue()).get(position));
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mSysLang.getValue()));
                break;
            case UNIT:
                mUnit.setValue(Objects.requireNonNull(mDatas.getValue()).get(position));
                mPosition.setValue(Objects.requireNonNull(mDatas.getValue()).indexOf(mUnit.getValue()));
                break;
        }
    }

    public void ok(SettingFragment.SetMode setMode) {
        switch (setMode) {
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
        }
    }

    public void cancel(SettingFragment.SetMode setMode) {
        switch (setMode) {
            case SYS_LANG:
                resetSysLang();
                break;
            case UNIT:
                resetUnit();
                break;
        }
    }
}