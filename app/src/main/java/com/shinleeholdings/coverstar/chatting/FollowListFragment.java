package com.shinleeholdings.coverstar.chatting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.databinding.FragmentChattingListBinding;
import com.shinleeholdings.coverstar.databinding.FragmentFollowBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.DefaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class FollowListFragment extends BaseFragment {

    private FragmentFollowBinding binding;
    // TODO 팔로우 아답터 설정

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
        // TODO 아답터 설정
    }

    private void requestData() {
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        RetroClient.getApiInterface().getFollow(param).enqueue(new RetroCallback<DefaultResult>() {
            @Override
            public void onSuccess(BaseResponse<DefaultResult> receivedData) {
                ProgressDialogHelper.dismiss();
                //            "data": [
//            {
//                "userId": "8201051391123",
//                    "userPwd": "*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9",
//                    "userType": 0,
//                    "nickName": "royyyy",
//                    "phoneNumber": "8201051391123",
//                    "userEmail": "cto@coverstar.com",
//                    "curCoin": "0",
//                    "userProfileImage": "https://d3fq0wmt9zn6f5.cloudfront.net/roy.jpg",
//                    "pcPush": null,
//                    "androidPush": null,
//                    "iosPush": "dfhjlwfhjlkwqjfldjwfjlwqjflkjqkwdl",
//                    "userDialCode": "82",
//                    "userNation": "KR",
//                    "recommend": "4178496412"
//            },
//        mAdapter.setData();
            }

            @Override
            public void onFailure(BaseResponse<DefaultResult> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }
}
