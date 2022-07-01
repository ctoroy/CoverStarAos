package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentCoverstarBinding;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class CoverStarListFragment extends BaseFragment {
    private FragmentCoverstarBinding binding;

    private CoverStarMediaListAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoverstarBinding.inflate(inflater, container, false);
        initView();
        requestData();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.coverStarSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.coverStarSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        mListAdapter = new CoverStarMediaListAdapter((MainActivity) getActivity());
        binding.coverStarRecyclerView.setItemViewCacheSize(200);
        binding.coverStarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.coverStarRecyclerView.setAdapter(mListAdapter);
    }

    private void requestData() {
        HashMap<String, String> param = new HashMap<>();
        param.put("temp", "a");
        RetroClient.getApiInterface().getStarList(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();
                binding.coverStarSwipeRefreshLayout.setRefreshing(false);
                ContestDataList itemList = receivedData.data;
                Util.sortList(mListAdapter.mSelectedSortType, itemList);
                mListAdapter.setData(itemList);
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                binding.coverStarSwipeRefreshLayout.setRefreshing(false);
                ProgressDialogHelper.dismiss();
            }
        });

        ProgressDialogHelper.dismiss();
    }
}
