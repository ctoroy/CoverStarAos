package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

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
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mListAdapter.getItemViewType(position)) {
                    case CoverStarMediaListAdapter.ITEM_TYPE_MEDIA_HEADER:
                        return 2;
                }

                return 1;
            }
        });
        binding.coverStarRecyclerView.setItemViewCacheSize(200);
        binding.coverStarRecyclerView.setLayoutManager(mLayoutManager);
        binding.coverStarRecyclerView.setAdapter(mListAdapter);
    }

    private void requestData() {
        // TODO API 연동 필요
        HashMap<String, String> param = new HashMap<>();
        param.put("contestId", "3");
        RetroClient.getApiInterface().getLastList(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();
                ContestDataList itemList = receivedData.data;
                Util.sortList(mListAdapter.mSelectedSortType, itemList);
                mListAdapter.setData(itemList);
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();

            }
        });

        ProgressDialogHelper.dismiss();
    }
}
