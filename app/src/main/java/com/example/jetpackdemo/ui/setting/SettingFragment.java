package com.example.jetpackdemo.ui.setting;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jetpackdemo.R;
import com.example.jetpackdemo.databinding.FragmentSettingBinding;
import com.example.jetpackdemo.databinding.ResetingViewBinding;
import com.example.jetpackdemo.databinding.SetDialogBinding;
import com.example.jetpackdemo.ui.adapter.ItemListAdapter;
import com.example.jetpackdemo.util.Utils;
import com.example.jetpackdemo.util.log.Logger;

import java.util.Objects;

public class SettingFragment extends Fragment {
    private Logger mLog = Logger.create("SettingFragment");

    private SettingViewModel mSettingViewModel;
    private FragmentSettingBinding mBinding;
    private SetDialogBinding mSetBind;

    private AlertDialog mSetDialog;
    private AlertDialog mResettingDialog;
    private Click mClick;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSettingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        mSettingViewModel.getVersion().observe(getViewLifecycleOwner(), version -> mBinding.fwVersion.setRightText(version));
        mSettingViewModel.getSysLang().observe(getViewLifecycleOwner(), lang -> mBinding.sysLang.setRightText(lang));
        mSettingViewModel.getUnit().observe(getViewLifecycleOwner(), unit -> mBinding.unit.setRightText(unit));
        mSettingViewModel.getWiFi().observe(getViewLifecycleOwner(), wifi -> mBinding.wifi.setRightText(wifi));
        mSettingViewModel.getWiFiMode().observe(getViewLifecycleOwner(), wifi_mode -> mBinding.wifiMode.setRightText(wifi_mode));
        mSettingViewModel.supportWiFiMode().observe(getViewLifecycleOwner(), support -> mBinding.wifiMode.setVisibility(support ? View.VISIBLE : View.GONE));
        mSettingViewModel.getSdTotal().observe(getViewLifecycleOwner(), total -> {
            if (total == 0) {
                mBinding.formatSdCard.setClickable(false);
                mBinding.formatSdCard.switchRightStyle(1);
                mBinding.formatSdCard.setRightText(getResources().getString(R.string.no_sdcard));
            } else {
                mBinding.formatSdCard.setClickable(true);
                mBinding.formatSdCard.switchRightStyle(0);
                mBinding.formatSdCard.setRightText(String.format(getResources().getString(R.string.sd_space), Utils.formatSize(total)));
            }
        });

        mBinding = FragmentSettingBinding.inflate(inflater, container, false);
        mBinding.setSettingViewModel(mSettingViewModel);
        mClick = new Click();
        mBinding.setClick(mClick);
        getLifecycle().addObserver(mSettingViewModel);

        return mBinding.getRoot();
    }

    enum SetMode {
        SET_WARNING_ALERT_TYPE,
        SET_CRITICAL_ALERT_TYPE,
        SET_MAX_SPEED,
        SET_TIRE_DIMENSION,
        SET_RADAR,
        SYS_LANG,
        FORMAT_SD,
        RESET,
        UNIT,
        WIFI_MODE,
        WIFI
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public class Click implements AdapterView.OnItemClickListener {

        public void setUnits() {
            mSettingViewModel.mode(SetMode.UNIT);
            showDialog();
        }

        public void setWiFi() {
            mSettingViewModel.mode(SetMode.WIFI);
            showDialog();
        }

        public void setWiFiMode() {
            mSettingViewModel.mode(SetMode.WIFI_MODE);
            showDialog();
        }

        public void formatSdCard() {
            mSettingViewModel.mode(SetMode.FORMAT_SD);
            showDialog();
        }

        public void reset() {
            mSettingViewModel.mode(SetMode.RESET);
            showDialog();
        }

        public void setSysLang() {
            mSettingViewModel.mode(SetMode.SYS_LANG);
            showDialog();
        }

        public void cancel() {
            mSettingViewModel.cancel();
            mSetDialog.dismiss();
        }

        public void ok() {
            switch (Objects.requireNonNull(mSettingViewModel.getMode().getValue())) {
                case WIFI:
                    String ssid = mSetBind.ssidEdit.getText().toString();
                    String psd = mSetBind.passwordEdit.getText().toString();
                    if (TextUtils.isEmpty(ssid)) {
                        Toast.makeText(getContext(), R.string.wifi_name_empty, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (ssid.length() > 16) {
                        Toast.makeText(getContext(), R.string.wifi_name_too_long, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (TextUtils.isEmpty(psd)) {
                        Toast.makeText(getContext(), R.string.psd_too_short, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (psd.length() < 8) {
                        Toast.makeText(getContext(), R.string.psd_too_short, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (psd.length() > 16) {
                        Toast.makeText(getContext(), R.string.psd_too_long, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    mSettingViewModel.setWiFiInfo(ssid, psd);
                    break;
                case RESET:
                    if (mResettingDialog == null) {
                        ResetingViewBinding bind = ResetingViewBinding.inflate(LayoutInflater.from(getContext()));
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(bind.getRoot());
                        mResettingDialog = builder.create();
                        mResettingDialog.setCanceledOnTouchOutside(false);
                    }
                    mResettingDialog.show();
                    break;
            }
            mSettingViewModel.ok();
            mSetDialog.dismiss();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSettingViewModel.select(position);
        }
    }

    private void showDialog() {
        if (mSettingViewModel.getDeviceInfo() == null) return;
        if (mSetDialog == null) {
            mSetBind = SetDialogBinding.inflate(LayoutInflater.from(getContext()));
            mSetBind.setClick(mClick);

            ItemListAdapter adapter = new ItemListAdapter(getContext(), mSettingViewModel.getDatas().getValue());
            mSetBind.list.setAdapter(adapter);
            mSetBind.list.setOnItemClickListener(mClick);

            mSettingViewModel.getWiFi().observe(getViewLifecycleOwner(), wifi -> {
                mSetBind.ssidEdit.setText(wifi);
                mSetBind.ssidEdit.setSelection(wifi.length());
            });

            mSettingViewModel.getPosition().observe(getViewLifecycleOwner(), adapter::select);
            mSettingViewModel.getTitle().observe(getViewLifecycleOwner(), mSetBind.title::setText);
            mSettingViewModel.getMsg().observe(getViewLifecycleOwner(), mSetBind.setMsg::setText);
            mSettingViewModel.getSetDeviceInfoRet().observe(getViewLifecycleOwner(), ret -> {
                if (ret == 0 && mSetDialog != null) {
                    Toast.makeText(getContext(), R.string.modify_success, Toast.LENGTH_SHORT).show();
                    mSetDialog.dismiss();
                }
            });

            mSettingViewModel.getResetRet().observe(getViewLifecycleOwner(), ret -> {
                if (ret == 0 && mResettingDialog != null) {
                    Toast.makeText(getContext(), R.string.reset_success, Toast.LENGTH_SHORT).show();
                    mResettingDialog.dismiss();
                }
            });

            mSettingViewModel.getMode().observe(getViewLifecycleOwner(), mode -> {
                mSetBind.title.setVisibility(View.GONE);
                mSetBind.listLl.setVisibility(View.GONE);
                mSetBind.funcLl.setVisibility(View.GONE);
                mSetBind.wifiLl.setVisibility(View.GONE);
                mSetBind.setMsg.setVisibility(View.GONE);
                switch (mode) {
                    case SYS_LANG:
                    case UNIT:
                        mSetBind.title.setVisibility(View.VISIBLE);
                        mSetBind.listLl.setVisibility(View.VISIBLE);
                        mSetBind.funcLl.setVisibility(View.VISIBLE);
                        break;
                    case WIFI:
                        mSetBind.title.setVisibility(View.VISIBLE);
                        mSetBind.wifiLl.setVisibility(View.VISIBLE);
                        mSetBind.funcLl.setVisibility(View.VISIBLE);
                        break;
                    case WIFI_MODE:
                        mSetBind.title.setVisibility(View.VISIBLE);
                        mSetBind.listLl.setVisibility(View.VISIBLE);
                        mSetBind.funcLl.setVisibility(View.VISIBLE);
                        mSetBind.setMsg.setVisibility(View.VISIBLE);
                        break;
                    case FORMAT_SD:
                    case RESET:
                        mSetBind.title.setVisibility(View.VISIBLE);
                        mSetBind.funcLl.setVisibility(View.VISIBLE);
                        mSetBind.setMsg.setVisibility(View.VISIBLE);
                        break;
                }
            });

            mSetDialog = new AlertDialog.Builder(getContext())
                    .setView(mSetBind.getRoot())
                    .show();
            mSetDialog.setCanceledOnTouchOutside(false);
        } else {
            mSetDialog.show();
        }
    }
}