package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentHomeBinding;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.tab_name_home));
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
