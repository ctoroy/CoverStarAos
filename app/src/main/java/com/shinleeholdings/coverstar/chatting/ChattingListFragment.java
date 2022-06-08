package com.shinleeholdings.coverstar.chatting;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.databinding.FragmentChattingListBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.Collections;

public class ChattingListFragment extends BaseFragment {

    private FragmentChattingListBinding binding;

    private ChatRoomListAdapter listAdapter;

    private final Handler searchHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChattingListBinding.inflate(inflater, container, false);
        initView();

        if (ChatRoomListHelper.getSingleInstance().isChattingListLoadingCompleted()) {
            updateChattingList();
        } else {
            ProgressDialogHelper.show(getActivity());
        }
        ChatRoomListHelper.getSingleInstance().addChattingRoomListListener(chattingRoomListListener);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatRoomListHelper.getSingleInstance().removeChattingRoomListListener(chattingRoomListListener);
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
        listAdapter = new ChatRoomListAdapter((MainActivity) getActivity());
        binding.chattingRoomListRecyclerView.setAdapter(listAdapter);
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

    private ChatRoomListHelper.IChattingRoomEventListener chattingRoomListListener = new ChatRoomListHelper.IChattingRoomEventListener() {
        @Override
        public void onChattingRoomLoadCompleted() {
            updateChattingList();
        }

        @Override
        public void onChattingRoomDetailInfoChange(String chatId, ChatRoomItem item) {
            DebugLogger.i("chattingRoomList", "requestChatList onChattingRoomDetailInfoChange chatId : " + chatId);
            updateChattingList();
        }

        @Override
        public void onChattingRoomAdded(String chatId, ChatRoomItem item) {
            DebugLogger.i("chattingRoomList", "requestChatList onChattingRoomAdded chatId : " + chatId);
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChattingRoomInfoChanged(String chatId) {
            DebugLogger.i("chattingRoomList", "requestChatList onChattingRoomInfoChanged chatId : " + chatId);
            if (listAdapter != null) {
                listAdapter.notifyDataChanged(chatId);
            }
        }

        @Override
        public void onChattingRoomRemoved(String chatId) {
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onMemberUpdate(String chatId) {
            DebugLogger.i("chattingRoomList", "setChattingRoomListListener onMemberUpdate chatId : " + chatId);
            if (listAdapter != null) {
                listAdapter.notifyDataChanged(chatId);
            }
        }
    };

    private Handler uiUpdateHandler = new Handler();

    private Runnable listUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            ArrayList<ChatRoomItem> chattingRoomList = ChatRoomListHelper.getSingleInstance().getChattingRoomList();
            DebugLogger.i("chattingRoomList", "updateChattingRoomList : " + chattingRoomList.size());

            if (chattingRoomList.size() <= 0) {
                binding.noChatRoomResultView.setVisibility(View.VISIBLE);
                binding.chattingRoomListRecyclerView.setVisibility(View.GONE);
            } else {
                binding.noChatRoomResultView.setVisibility(View.GONE);
                binding.chattingRoomListRecyclerView.setVisibility(View.VISIBLE);
                Collections.sort(chattingRoomList);
                listAdapter.setData(chattingRoomList);
            }
            ProgressDialogHelper.dismiss();
        }
    };

    private void updateChattingList() {
        DebugLogger.i("chattingRoomList", "updateChattingList");
        uiUpdateHandler.removeCallbacks(listUpdateRunnable);
        uiUpdateHandler.postDelayed(listUpdateRunnable, 150);
    }
}
