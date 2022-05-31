package com.shinleeholdings.coverstar.chatting;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.databinding.FragmentChattingListBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

public class ChattingListFragment extends BaseFragment {

    private FragmentChattingListBinding binding;

    private final Handler searchHandler = new Handler();

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

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                final String requestKeyword = charSequence.toString();
                searchRunnable = () -> requestSearch(requestKeyword);
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.chattingRoomListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO 채팅 리스트 아답터 설정
    }

    private Runnable searchRunnable;

    private void requestSearch(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            return;
        }

        if (NetworkHelper.isNetworkConnected() == false) {
            return;
        }

        // TODO 채팅방 검색
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
