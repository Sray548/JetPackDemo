package com.example.jetpackdemo.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jetpackdemo.databinding.FragmentSettingBinding;
import com.example.jetpackdemo.util.log.Logger;

public class SettingFragment extends Fragment {
    private Logger mLog = Logger.create("SettingFragment");

    private SettingViewModel settingViewModel;
    private FragmentSettingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);
        settingViewModel.getVersion().observe(getViewLifecycleOwner(), version -> binding.fwVersion.setRightText(version));
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        binding.setSettingViewModel(settingViewModel);
        getLifecycle().addObserver(settingViewModel);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}