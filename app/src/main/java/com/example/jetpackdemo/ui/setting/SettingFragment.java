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

import com.example.jetpackdemo.R;
import com.example.jetpackdemo.databinding.FragmentSettingBinding;
import com.example.jetpackdemo.databinding.SetDialogBinding;
import com.example.jetpackdemo.ui.adapter.ItemListAdapter;
import com.example.jetpackdemo.util.log.Logger;

import java.util.Objects;

public class SettingFragment extends Fragment {
    private Logger mLog = Logger.create("SettingFragment");

    private SettingViewModel mSettingViewModel;
    private FragmentSettingBinding binding;

    private AlertDialog mSetDialog;
    private Click mClick;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSettingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        mSettingViewModel.getVersion().observe(getViewLifecycleOwner(), version -> binding.fwVersion.setRightText(version));
        mSettingViewModel.getSysLang().observe(getViewLifecycleOwner(), lang -> binding.sysLang.setRightText(lang));

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        binding.setSettingViewModel(mSettingViewModel);
        mClick = new Click();
        binding.setClick(mClick);
        getLifecycle().addObserver(mSettingViewModel);

        View root = binding.getRoot();

        return root;
    }

    private SetMode mSetMode;

    private enum SetMode {
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
        public void setSysLang() {
            mSetMode = SetMode.SYS_LANG;
            mSettingViewModel.initData();
            showDialog();
        }

        public void cancel() {
            mSettingViewModel.resetSysLang();
            mSetDialog.dismiss();
        }

        public void ok() {
            mSettingViewModel.setSysLang();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSettingViewModel.selectSysLang(position);
        }
    }

    private void showDialog() {
        if (mSetDialog == null) {
            SetDialogBinding bind = SetDialogBinding.inflate(LayoutInflater.from(getContext()));
            bind.setClick(mClick);
            bind.title.setText(R.string.sys_lang);

            ItemListAdapter adapter = new ItemListAdapter(getContext(), mSettingViewModel.getDatas().getValue());
            bind.list.setAdapter(adapter);
            bind.list.setOnItemClickListener(mClick);

            mSettingViewModel.getSysLang().observe(getViewLifecycleOwner(), lang ->
                    adapter.select(Objects.requireNonNull(mSettingViewModel.getDatas().getValue()).indexOf(mSettingViewModel.getSysLang().getValue())));
            mSettingViewModel.getRet().observe(getViewLifecycleOwner(), ret -> {
                if (ret == 0 && mSetDialog != null) {
                    mSetDialog.dismiss();
                    Toast.makeText(getContext(), R.string.modify_success, Toast.LENGTH_SHORT).show();
                }
            });

            mSetDialog = new AlertDialog.Builder(getContext())
                    .setView(bind.getRoot())
                    .show();
            mSetDialog.setCanceledOnTouchOutside(false);
        } else {
            mSetDialog.show();
        }
    }
}