package com.shinleeholdings.coverstar.chatting;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityChatBinding;
import com.shinleeholdings.coverstar.service.MessagingService;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

import network.model.BaseResponse;
import network.retrofit.RetroCallback;

public class ChatActivity extends BaseActivity {

    private String chattingId;

    private RecyclerView chattingListView;
    private ChattingListAdapter chattingListAdapter;

    private LinearLayoutManager linearLayoutManager;

    private ChatMessageListHelper chattingHelper;

    private String lastLoadMoreMessageKey = "";

    ChatRoomItem chattingRoomInfo;

    boolean fromNoti = false;

    private ActivityChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chattingId = getIntent().getStringExtra(AppConstants.EXTRA.CHAT_ID);
        fromNoti = getIntent().getBooleanExtra(AppConstants.EXTRA.FROM_NOTI, false);
        chattingHelper = ChatMessageListHelper.getSingleInstance();
        initView();

        startChatting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chattingHelper.setCurrentChattingId(chattingId);
    }

    private void initView() {
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromNoti) {
                    onBackPressed();
                } else {
                    Util.hideKeyboard(ChatActivity.this);
                    finish();
                }
            }
        });

        binding.layoutChattingroom.setOnResizeEventListener(new ChattingRoomLayout.ViewResizeEventListener() {

            @Override
            public void onResized() {
                if (chattingListView == null || chattingListAdapter == null) {
                    return;
                }

                int getCount = chattingListAdapter.getItemCount();
                if (getCount <= 0) {
                    return;
                }

                int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition() + 1;
                if (getCount <= lastVisiblePosition) {
                    scrollToLastListItem();
                }
            }
        });

        chattingListView = binding.layoutChattingroom.findViewById(R.id.chatting_listview);
        ((SimpleItemAnimator) chattingListView.getItemAnimator()).setSupportsChangeAnimations(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chattingListAdapter = new ChattingListAdapter(this);
        chattingListView.setAdapter(chattingListAdapter);
        chattingListView.setLayoutManager(linearLayoutManager);
        chattingListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itemCount = chattingListAdapter.getItemCount();
                if (dy < 0) {
                    if (itemCount > 0 && linearLayoutManager.findFirstVisibleItemPosition() < 5) {
                        loadPrevMessage(true);
                    }
                }
            }
        });

        binding.chattingEdittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String editTextString = s.toString();
                binding.sendTextView.setEnabled(TextUtils.isEmpty(editTextString) == false);
            }
        });

        binding.sendTextView.setEnabled(false);
        binding.sendTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chattingRoomInfo == null || NetworkHelper.isNetworkConnected() == false) {
                    Toast.makeText(ChatActivity.this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatItem item = getChatItem(binding.chattingEdittext.getText().toString());
                ChattingItem chattingItem = new ChattingItem("", item, chattingId, ChattingItem.SENDSTATE.SENDING);
                sendMessage(chattingItem);
            }
        });
    }

    private ChatItem getChatItem(String message) {
        ArrayList<String> userIdList = new ArrayList<>();
        for (ChattingRoomMember member : chattingRoomInfo.getMemberList()) {
            String userId = member.getUserId();
            if (userId.equals(LoginHelper.getSingleInstance().getLoginUserId()) == false) {
                userIdList.add(userId);
            }
        }

        return new ChatItem(System.currentTimeMillis() + "",
                message, ChattingConstants.MSG_TYPE_MESSAGE, userIdList,
                LoginHelper.getSingleInstance().getLoginUserId(),
                LoginHelper.getSingleInstance().getLoginUserNickName(),
                LoginHelper.getSingleInstance().getLoginUserImagePath());
    }

    private boolean isLastItemCompletelyVisible() {
        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() >= chattingListAdapter.getItemCount() - 2) {
            return true;
        } else {
            return false;
        }
    }

    private void sendMessage(final ChattingItem item) {
        // 채팅창에 표시
        chattingListAdapter.addItem(item);
        binding.chattingEdittext.setText("");

        chattingHelper.saveChattingMessageToDataBase(item);
        chattingHelper.sendChattingMessageToServer(chattingId, item, new RetroCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse receivedData) {
                if (isFinishing()) {
                    DebugLogger.e("test", "onMessage sended but chattingRoom is not showing");
                    ChatMessageListHelper.getSingleInstance().deleteChattingItem(item);
                }
            }

            @Override
            public void onFailure(BaseResponse response) {
                setMessageSendFail(item);
            }
        });

        scrollToLastListItem();
    }

    private void startChatting() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MessagingService.PUSH_GROUP_ID + "", chattingId.hashCode());

        chattingHelper.setCurrentChattingId(chattingId);

        if (ChatRoomListHelper.getSingleInstance().isChattingListLoadingCompleted()) {
            updateChattingRoomInfo();
            loadPrevMessage(false);
        } else {
            ProgressDialogHelper.show(this);
            ChatRoomListHelper.getSingleInstance().addChattingRoomListListener(chattingRoomListListener);
        }
    }

    private ChatRoomListHelper.IChattingRoomEventListener chattingRoomListListener = new ChatRoomListHelper.IChattingRoomEventListener() {
        @Override
        public void onChattingRoomLoadCompleted() {
            ProgressDialogHelper.dismiss();
            ChatRoomListHelper.getSingleInstance().removeChattingRoomListListener(chattingRoomListListener);
            updateChattingRoomInfo();
            loadPrevMessage(false);
        }

        @Override
        public void onChattingRoomDetailInfoChange(String chatId, ChatRoomItem item) {
        }

        @Override
        public void onChattingRoomAdded(String chatId, ChatRoomItem item) {
        }

        @Override
        public void onChattingRoomInfoChanged(String chatId) {
        }

        @Override
        public void onChattingRoomRemoved(String chatId) {
        }

        @Override
        public void onMemberUpdate(String chatId) {
        }
    };

    private void updateChattingRoomInfo() {
        chattingRoomInfo = ChatRoomListHelper.getSingleInstance().getChatRoomItem(chattingId);
        if (chattingRoomInfo == null) {
            return;
        }

        binding.titleLayout.titleTextView.setText(chattingRoomInfo.getDisplayRoomName());
    }

    private void initMyBadgeCount() {
        long badgeCount = BadgeManager.getSingleInstance().getChattingRoomBadge(chattingId);
        if (badgeCount > 0) {
            ChatRoomListHelper.getSingleInstance().updateChatListInfo(chattingId, ChattingConstants.FIELDNAME_BADGE_CNT, 0);
        }
    }

    private void loadPrevMessage(final boolean loadMore) {
        String firstItemKey = "";

        if (chattingListAdapter.getItemCount() > 0) {
            ChattingItem firstItem = chattingListAdapter.getItem(0);
            firstItemKey = firstItem.getChatItem().key;
        }

        if (TextUtils.isEmpty(lastLoadMoreMessageKey) == false && lastLoadMoreMessageKey.equals(firstItemKey)) {
            DebugLogger.e("test", "already request prevMessage : " + firstItemKey);
            return;
        }

        lastLoadMoreMessageKey = firstItemKey;
        chattingHelper.getPrevMessage(firstItemKey, true, new ChatMessageListHelper.IGetMessageListListener() {
            @Override
            public void onMessageListLoaded(ArrayList<ChattingItem> itemList) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (loadMore) {
                    chattingListAdapter.addPrevChattingListData(itemList);
                    return;
                }

                chattingListAdapter.setListData(itemList);

                ArrayList<ChattingItem> notSendedChattingItemList = chattingHelper.getSendFailMessageList();
                chattingListAdapter.setSendFailMessageListData(notSendedChattingItemList);
                chattingListAdapter.notifyDataSetChanged();

                scrollToLastListItem();
                addChattingEventListener();
                chattingHelper.updateMessageRead(itemList);
            }
        });
    }

    private void scrollToLastListItem() {
        final int listSize = chattingListAdapter.getItemCount();
        if (listSize <= 0 || ChatActivity.this.isFinishing() || linearLayoutManager == null || chattingListView == null) {
            return;
        }

        // 정확한 포지션을 넘겨야 동작
        int scrollPosition = listSize - 1;
        linearLayoutManager.scrollToPositionWithOffset(scrollPosition, 0);
    }

    private void addChattingEventListener() {
        DebugLogger.i("test", "addChattingEventListener");
        initMyBadgeCount();

        final ChattingItem lastMessageItem = chattingListAdapter.getLastMessageItem();

        chattingHelper.addChattingEventListener(lastMessageItem, new ChatMessageListHelper.IChattingItemEventListener() {
            @Override
            public void onItemAdded(ChattingItem item) {
                if (isFinishing()) {
                    return;
                }

                int getItemIndex = -1;
                if (item.isMyChat()) {
                    chattingHelper.deleteChattingItem(item);
                    getItemIndex = chattingListAdapter.getItemIndex(item);
                }

                chattingListAdapter.addListItem(item, getItemIndex);
                chattingListAdapter.notifyDataSetChanged();

                if (chattingListView.isComputingLayout() == false) {
                    if (isLastItemCompletelyVisible()) {
                        scrollToLastListItem();
                    }
                }

                if (item.isMemberChangeMessage() == false) {
                    ArrayList<ChattingItem> itemList = new ArrayList<>();
                    itemList.add(item);
                    chattingHelper.updateMessageRead(itemList);
                }
            }

            @Override
            public void onItemChanged(ChattingItem item) {
                if (isFinishing()) {
                    return;
                }
                DebugLogger.e("test", "onChattingItemChanged : " + item);
                if (chattingListAdapter != null) {
                    chattingListAdapter.changeListItem(item);
                }

                ArrayList<ChattingItem> itemList = new ArrayList<>();
                itemList.add(item);
                chattingHelper.updateMessageRead(itemList);
            }

            @Override
            public void onItemRemoved(ChattingItem item) {
                if (isFinishing()) {
                    return;
                }

                if (chattingListAdapter != null) {
                    chattingListAdapter.onItemRemoved(item);
                }
            }

            @Override
            public void onError(String message) {
                DebugLogger.e("test", "onChattingItemonError : " + message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chattingHelper.removeChattingEventListener();
        ChatRoomListHelper.getSingleInstance().removeChattingRoomListListener(chattingRoomListListener);
    }

    @Override
    public void onBackPressed() {
        if (fromNoti) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        super.onBackPressed();
    }

    private void setMessageSendFail(ChattingItem item) {
        item.setSendState(ChattingItem.SENDSTATE.FAIL);
        ChatMessageListHelper.getSingleInstance().saveChattingMessageToDataBase(item);

        if (isFinishing()) {
            return;
        }

        if (chattingListAdapter != null) {
            chattingListAdapter.increaseSendFailMessageCount();
            chattingListAdapter.notifyDataSetChanged();
        }
    }

    public void resendChatItem(ChattingItem currentChatItem) {
        if (chattingRoomInfo == null || NetworkHelper.isNetworkConnected() == false) {
            Toast.makeText(ChatActivity.this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        chattingHelper.deleteChattingItem(currentChatItem);
        chattingListAdapter.removeSendFailMessage(currentChatItem);

        ChatItem chatItem = currentChatItem.getChatItem();
        ArrayList<String> userIdList = new ArrayList<>();
        for (ChattingRoomMember member : chattingRoomInfo.getMemberList()) {
            String userId = member.getUserId();
            if (userId.equals(LoginHelper.getSingleInstance().getLoginUserId()) == false) {
                userIdList.add(userId);
            }
        }

        chatItem.not_read = userIdList;
        chatItem.key = System.currentTimeMillis() + "";
        chatItem.user_name = LoginHelper.getSingleInstance().getLoginUserNickName();
        chatItem.user_photo = LoginHelper.getSingleInstance().getLoginUserImagePath();

        currentChatItem.setSendState(ChattingItem.SENDSTATE.SENDING);
        sendMessage(currentChatItem);
    }
}
