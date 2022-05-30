package com.shinleeholdings.coverstar.chatting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.databinding.FragmentChattingListBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

public class FollowListFragment extends BaseFragment {

    private FragmentChattingListBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChattingListBinding.inflate(inflater, container, false);
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

        binding.chattingRoomListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO 아답터
    }

    private void requestData() {
        ProgressDialogHelper.show(getActivity());
//        mAdapter.setData();
//        ***get follow list
//                /coverstarAPI/getFollow?userId=8201012345678
//        @RequestParam("userId") String userId
//
//        {
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
        ProgressDialogHelper.dismiss();
    }
}
