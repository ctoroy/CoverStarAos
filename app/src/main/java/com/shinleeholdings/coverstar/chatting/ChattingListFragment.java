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

public class ChattingListFragment extends BaseFragment {

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
        binding.newChattingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new FollowListFragment());
            }
        });

        binding.chattingRoomListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO 채팅 리스트 아답터 설정
    }

    private void requestData() {
        ProgressDialogHelper.show(getActivity());
        // TODO 채팅 목록 불러오기
        // TODO 빈화면 설정
//        mAdapter.setData();
        ProgressDialogHelper.dismiss();
    }

    // 채팅 방 리스트 구조
}
