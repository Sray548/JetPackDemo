package com.example.jetpackdemo.ui.setting;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jetpackdemo.databinding.FragmentSettingBinding;
import com.example.jetpackdemo.databinding.SetDialogBinding;
import com.example.jetpackdemo.ui.adapter.ItemListAdapter;
import com.example.jetpackdemo.util.log.Logger;

import java.util.Objects;

public class SettingFragment extends Fragment {
    private Logger mLog = Logger.create("SettingFragment");

    private SettingViewModel mSettingViewModel;
    private FragmentSettingBinding binding;
    private SetDialogBinding mSetBind;

    private AlertDialog mSetDialog;
    private Click mClick;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSettingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        mSettingViewModel.getVersion().observe(getViewLifecycleOwner(), version -> binding.fwVersion.setRightText(version));
        mSettingViewModel.getSysLang().observe(getViewLifecycleOwner(), lang -> binding.sysLang.setRightText(lang));
        mSettingViewModel.getUnit().observe(getViewLifecycleOwner(), unit -> binding.unit.setRightText(unit));
        mSettingViewModel.getWiFi().observe(getViewLifecycleOwner(), wifi -> binding.wifi.setRightText(wifi));

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        binding.setSettingViewModel(mSettingViewModel);
        mClick = new Click();
        binding.setClick(mClick);
        getLifecycle().addObserver(mSettingViewModel);

        View root = binding.getRoot();

        return root;
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
        binding = null;
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
                    mSettingViewModel.setWiFiInfo(mSetBind.ssidEdit.getText().toString(), mSetBind.passwordEdit.getText().toString());
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
            mSettingViewModel.getRet().observe(getViewLifecycleOwner(), ret -> {
                if (ret == 0 && mSetDialog != null) {
                    mSetDialog.dismiss();
                }
            });

            mSettingViewModel.getMode().observe(getViewLifecycleOwner(), mode -> {
                mSetBind.title.setVisibility(View.GONE);
                mSetBind.listLl.setVisibility(View.GONE);
                mSetBind.funcLl.setVisibility(View.GONE);
                mSetBind.wifiLl.setVisibility(View.GONE);
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