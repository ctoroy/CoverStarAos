package com.shinleeholdings.coverstar.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentChangeLanguageBinding;
import com.shinleeholdings.coverstar.databinding.FragmentDepositeBinding;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.LanguageHelper;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;
import com.shinleeholdings.coverstar.util.Util;

public class LanguageChangeFragment extends BaseFragment {

    private FragmentChangeLanguageBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChangeLanguageBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.change_language));
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (LanguageHelper.LANGUAGE_EN.equals(SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LANGUAGE))) {
            binding.englishSelectedImageView.setVisibility(View.VISIBLE);
            binding.koreanSelectedImageView.setVisibility(View.GONE);
        } else {
            binding.koreanSelectedImageView.setVisibility(View.VISIBLE);
            binding.englishSelectedImageView.setVisibility(View.GONE);
        }

        binding.koreanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LanguageHelper.LANGUAGE_KR.equals(SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LANGUAGE))) {
                    return;
                }

                SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.LANGUAGE, LanguageHelper.LANGUAGE_KR);
                DialogHelper.showMessagePopup(getActivity(), getString(R.string.app_restart_need), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Util.appExit(getActivity());
                    }
                });
            }
        });

        binding.englishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LanguageHelper.LANGUAGE_EN.equals(SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LANGUAGE))) {
                    return;
                }

                SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.LANGUAGE, LanguageHelper.LANGUAGE_EN);
                DialogHelper.showMessagePopup(getActivity(), getString(R.string.app_restart_need), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Util.appExit(getActivity());
                    }
                });
            }
        });
    }
}
