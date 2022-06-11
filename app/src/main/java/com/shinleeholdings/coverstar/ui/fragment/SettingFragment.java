package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.chatting.ChatRoomListHelper;
import com.shinleeholdings.coverstar.databinding.FragmentSettingBinding;
import com.shinleeholdings.coverstar.profile.LaunchActivity;
import com.shinleeholdings.coverstar.profile.ProfileSettingActivity;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;
import com.shinleeholdings.coverstar.util.Util;

public class SettingFragment extends BaseFragment {

    private FragmentSettingBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.setting));
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.editProfileLayout.setOnClickListener(view -> startActivity(new Intent(getActivity(), ProfileSettingActivity.class)));

        binding.noticeLayout.setOnClickListener(view -> addFragment(new NoticeListFragment()));

        binding.alarmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.alarmLayout.isSelected()) {
                    binding.alarmLayout.setSelected(false);
                    SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.ALARM_IS_OFF, true);
                } else {
                    binding.alarmLayout.setSelected(true);
                    SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.ALARM_IS_OFF, false);
                }
            }
        });

        boolean alarmIsOff = SharedPreferenceHelper.getInstance().getBooleanPreference(SharedPreferenceHelper.ALARM_IS_OFF);
        binding.alarmLayout.setSelected(!alarmIsOff);

        try {
            PackageInfo pInfo = getActivity().getApplicationContext().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            binding.appVersionCodeTextView.setText(pInfo.versionName);
        } catch (Exception e) {
        }

        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatRoomListHelper.getSingleInstance().initChattingListListener();
                SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.LOGIN_PW, "");

                Intent intent = new Intent(getActivity(), LaunchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }
}
