package com.shinleeholdings.coverstar.chatting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.databinding.FragmentFollowBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.CoverStarUserList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class FollowListFragment extends BaseFragment {

    private FragmentFollowBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFollowBinding.inflate(inflater, container, false);
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
        binding.titleBackLayout.setOnClickListener(view -> finish());

        binding.followListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO 팔로우 아답터 설정

        // TODO 클릭시 채팅방 오픈
    }

    private void requestData() {
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        RetroClient.getApiInterface().getFollow(param).enqueue(new RetroCallback<CoverStarUserList>() {
            @Override
            public void onSuccess(BaseResponse<CoverStarUserList> receivedData) {
                ProgressDialogHelper.dismiss();

                CoverStarUserList userList = receivedData.data;
                if (userList == null || userList.size() <= 0) {
                    // TODO 빈화면
                } else {
                    // TODO 아답터 설정
//        mAdapter.setData();
                }
            }

            @Override
            public void onFailure(BaseResponse<CoverStarUserList> response) {
                ProgressDialogHelper.dismiss();
                // TODO 빈화면
            }
        });
    }
}
