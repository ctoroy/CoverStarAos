package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentNoticelistBinding;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.NoticeDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class NoticeListFragment extends BaseFragment {

    private FragmentNoticelistBinding binding;

    private NoticeListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNoticelistBinding.inflate(inflater, container, false);
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
        binding.titleLayout.titleTextView.setText(getString(R.string.notice));
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

        binding.noticeListSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.noticeListSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        binding.noticeListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new NoticeListAdapter();
        binding.noticeListRecyclerView.setAdapter(mAdapter);
    }

    private void requestData() {
        binding.noticeListSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        param.put("temp", "1");
        RetroClient.getApiInterface().getNoticeList(param).enqueue(new RetroCallback<NoticeDataList>() {
            @Override
            public void onSuccess(BaseResponse<NoticeDataList> receivedData) {
                ProgressDialogHelper.dismiss();
                mAdapter.setData(receivedData.data);
            }

            @Override
            public void onFailure(BaseResponse<NoticeDataList> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }
}
