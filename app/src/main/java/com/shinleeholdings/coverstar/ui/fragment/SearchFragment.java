package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentSearchBinding;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding binding;
    private ContestListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {

        binding.titleLayout.titleTextView.setText(getString(R.string.search));
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

        binding.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    requestSearch();
                }
                return false;
            }
        });

        binding.searchImageView.setOnClickListener(view -> requestSearch());

        binding.searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ContestListAdapter((MainActivity) getActivity());

        binding.searchResultRecyclerView.setAdapter(mAdapter);
    }

    private void requestSearch() {
        String inputText = binding.searchEditText.getText().toString();
        if (TextUtils.isEmpty(inputText)) {
            binding.searchResultRecyclerView.setVisibility(View.GONE);
            binding.noSearchResultView.setVisibility(View.GONE);
            return;
        }

        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        param.put("keyword", inputText);
        RetroClient.getApiInterface().getLiveListName(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();
                ContestDataList itemList = receivedData.data;

                if (itemList.size() <= 0) {
                    binding.searchResultRecyclerView.setVisibility(View.GONE);
                    binding.noSearchResultView.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.setData(itemList);
                    binding.searchResultRecyclerView.setVisibility(View.VISIBLE);
                    binding.noSearchResultView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();
                binding.searchResultRecyclerView.setVisibility(View.GONE);
                binding.noSearchResultView.setVisibility(View.VISIBLE);
            }
        });
    }
}
