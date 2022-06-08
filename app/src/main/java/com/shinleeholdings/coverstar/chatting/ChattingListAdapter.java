package com.shinleeholdings.coverstar.chatting;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.ui.CircleImageView;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.LoginHelper;

import java.util.ArrayList;

public class ChattingListAdapter extends RecyclerView.Adapter {

    private final ArrayList<ChattingItem> itemList = new ArrayList<ChattingItem>();

    private ChatActivity mChatActivity;

    private int sendFailMessageCount = 0;

    public ChattingListAdapter(ChatActivity activity) {
        mChatActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ChattingItem currentChatItem = getItem(position);

        ItemViewHolder chattingItemViewHolder = (ItemViewHolder) holder;

        chattingItemViewHolder.sendMessageLayout.setVisibility(View.GONE);
        chattingItemViewHolder.receiveMessageLayout.setVisibility(View.GONE);
        chattingItemViewHolder.dateLineLayout.setVisibility(View.GONE);

        if (currentChatItem.isMyChat()) {
            setSendedMessage(chattingItemViewHolder, currentChatItem, position);
        } else {
            setReceivedMessage(chattingItemViewHolder, currentChatItem, position);
        }
        setTimeView(chattingItemViewHolder, currentChatItem, position);

    }

    private void setSendedMessage(ItemViewHolder chattingItemViewHolder, final ChattingItem currentChatItem, int position) {
        chattingItemViewHolder.receiveMessageLayout.setVisibility(View.GONE);
        chattingItemViewHolder.sendMessageLayout.setVisibility(View.VISIBLE);
        chattingItemViewHolder.dateLineLayout.setVisibility(View.GONE);
        chattingItemViewHolder.sendMessageItem.setText(currentChatItem.getChatItem().msg);
        chattingItemViewHolder.sendTimeTextView.setVisibility(View.VISIBLE);
        chattingItemViewHolder.sendFailImageView.setVisibility(View.GONE);
        chattingItemViewHolder.sendFailTempTextView.setVisibility(View.GONE);
        chattingItemViewHolder.sendFailTextView.setVisibility(View.GONE);
        String msgDisplayTimeText = currentChatItem.getMessageTimeDisplayText();

        ChattingItem.SENDSTATE sendState = currentChatItem.getSendState();
        switch (sendState) {
            case SUCCESS:
                boolean isSameTimeMessage = false;
                if (position + 1 < getItemCount()) {
                    ChattingItem nextItem = getItem(position + 1);
                    if (LoginHelper.getSingleInstance().getLoginUserId().equals(nextItem.getChatItem().user_id)) {
                        String nextMsgTime = nextItem.getMessageTimeDisplayText();
                        isSameTimeMessage = nextMsgTime.equals(msgDisplayTimeText) && nextItem.getSendState() == ChattingItem.SENDSTATE.SUCCESS;
                    }
                }
                if (isSameTimeMessage) {
                    chattingItemViewHolder.sendTimeTextView.setText("");
                } else {
                    chattingItemViewHolder.sendTimeTextView.setText(msgDisplayTimeText);
                }
                break;
            case SENDING:
                chattingItemViewHolder.sendTimeTextView.setText("전송중...");
                break;
            case FAIL:
                chattingItemViewHolder.sendTimeTextView.setText(msgDisplayTimeText);
                chattingItemViewHolder.sendFailImageView.setVisibility(View.VISIBLE);
                chattingItemViewHolder.sendFailTempTextView.setVisibility(View.VISIBLE);
                chattingItemViewHolder.sendFailTextView.setVisibility(View.VISIBLE);
                chattingItemViewHolder.sendFailTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.showTwoButtonMessagePopup(mChatActivity, "안내", "재전송 하시겠습니까?", "재전송", "삭제", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mChatActivity != null) {
                                    mChatActivity.resendChatItem(currentChatItem);
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 삭제
                                ChatMessageListHelper.getSingleInstance().deleteChattingItem(currentChatItem);
                                removeSendFailMessage(currentChatItem);
                            }
                        });
                    }
                });
                break;
        }
    }

    private void setReceivedMessage(ItemViewHolder chattingItemViewHolder, ChattingItem currentChatItem, int position) {
        chattingItemViewHolder.receiveMessageLayout.setVisibility(View.VISIBLE);
        chattingItemViewHolder.sendMessageLayout.setVisibility(View.GONE);
        chattingItemViewHolder.dateLineLayout.setVisibility(View.GONE);

        String msgTime = currentChatItem.getMessageTimeDisplayText();
        boolean isPrevMessageSameTime = false;
        boolean isNextMessageSameTime = false;

        if (position > 0) {
            ChattingItem prevItem = getItem(position - 1);
            if (currentChatItem.getChatItem().user_id.equals(prevItem.getChatItem().user_id)) {
                if (prevItem.isMyChat() == false) {
                    String prevMsgTime = prevItem.getMessageTimeDisplayText();
                    isPrevMessageSameTime = prevMsgTime.equals(msgTime);
                }
            }
        }

        if (position + 1 < getItemCount()) {
            ChattingItem nextItem = getItem(position + 1);
            if (currentChatItem.getChatItem().user_id.equals(nextItem.getChatItem().user_id)) {
                if (nextItem.isMyChat() == false) {
                    String nextMsgTime = nextItem.getMessageTimeDisplayText();
                    isNextMessageSameTime = nextMsgTime.equals(msgTime);
                }
            }
        }

        chattingItemViewHolder.receiveMessageItem.setText(currentChatItem.getChatItem().msg);

        if (isPrevMessageSameTime) {
            chattingItemViewHolder.userNickNameTextView.setVisibility(View.GONE);
            chattingItemViewHolder.otherProfileImageView.setVisibility(View.GONE);
        } else {
            chattingItemViewHolder.userNickNameTextView.setVisibility(View.VISIBLE);
            chattingItemViewHolder.userNickNameTextView.setText(currentChatItem.getChatItem().user_name);
            chattingItemViewHolder.otherProfileImageView.setVisibility(View.VISIBLE);
            ImageLoader.loadUserImage(chattingItemViewHolder.otherProfileImageView, currentChatItem.getImagePath());
        }

        if (isNextMessageSameTime) {
            chattingItemViewHolder.receiveTimeTextView.setVisibility(View.GONE);
        } else {
            chattingItemViewHolder.receiveTimeTextView.setVisibility(View.VISIBLE);
            chattingItemViewHolder.receiveTimeTextView.setText(msgTime);
        }
    }

    private void setTimeView(ItemViewHolder viewHolder, ChattingItem currentChatItem, int position) {
        String currentMessageDate = currentChatItem.getTimeLineDateText();
        if (position > 0) {
            ChattingItem prevItem = getItem(position - 1);
            if (prevItem == null) {
                return;
            }

            String prevMessageDate = prevItem.getTimeLineDateText();

            if (currentMessageDate.equals(prevMessageDate)) {
                return;
            }
        }
        viewHolder.dateLineLayout.setVisibility(View.VISIBLE);
        viewHolder.dateTextView.setText(currentMessageDate);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public ChattingItem getItem(int position) {
        return itemList.get(position);
    }

    public ChattingItem getLastMessageItem() {
        int count = getItemCount();
        if (count <= 0) {
            return null;
        }

        for (int i = count - 1; i >= 0; i--) {
            ChattingItem item = itemList.get(i);
            if (item.getSendState() != ChattingItem.SENDSTATE.SUCCESS) {
                continue;
            }

            String key = item.getTimeStampKey();
            if (TextUtils.isEmpty(key) == false) {
                return item;
            }
        }

        return null;
    }

    public void setListData(ArrayList<ChattingItem> chattingList) {
        itemList.clear();

        if (chattingList != null && chattingList.size() > 0) {
            itemList.addAll(chattingList);
        }
    }

    public void setSendFailMessageListData(ArrayList<ChattingItem> chattingList) {
        sendFailMessageCount = 0;
        if (chattingList == null || chattingList.size() <= 0) {
            return;
        }

        sendFailMessageCount = chattingList.size();
        itemList.addAll(chattingList);
    }

    public void increaseSendFailMessageCount() {
        sendFailMessageCount = sendFailMessageCount + 1;
    }

    private void decreaseSendFailMessageCount() {
        sendFailMessageCount = sendFailMessageCount - 1;
    }

    public void removeSendFailMessage(ChattingItem item) {
        if (item == null) {
            return;
        }

        decreaseSendFailMessageCount();
        itemList.remove(item);
        notifyDataSetChanged();
    }

    public void addPrevChattingListData(ArrayList<ChattingItem> chattingList) {
        if (chattingList == null || chattingList.size() <= 0) {
            return;
        }

        itemList.addAll(0, chattingList);
        notifyItemRangeInserted(0, chattingList.size());
    }

    public void addItem(ChattingItem item) {
        itemList.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public int getItemIndex(ChattingItem target) {
        if (target == null) {
            return -1;
        }

        int count = getItemCount();
        String targetItemKey = target.getTimeStampKey();
        for (int i = count - 1; i >= 0; i--) {
            ChattingItem item = itemList.get(i);
            String itemKey = item.getTimeStampKey();
            if (targetItemKey.equals(itemKey)) {
                return i;
            }
        }

        return -1;
    }

    public void addListItem(ChattingItem target, int itemIndex) {
        if (target == null) {
            return;
        }

        synchronized (itemList) {
            int count = getItemCount();
            if (count == 0) {
                itemList.add(target);
            } else {
                if (sendFailMessageCount == count) {
                    itemList.add(0, target);
                } else {
                    int itemAddIndex = count - 1;

                    for (int i = count - 1; i >= 0; i--) {
                        ChattingItem item = itemList.get(i);
                        if (item.getSendState() != ChattingItem.SENDSTATE.SUCCESS) {
                            itemAddIndex = i;
                            continue;
                        }

                        long targetDate = Long.parseLong(target.getChatItem().cdate);
                        long itemDate = Long.parseLong(item.getChatItem().cdate);

                        if (targetDate > itemDate) {
                            if (i < count - 1) {
                                itemAddIndex = i + 1;
                            }
                            break;
                        }
                    }

                    if (itemAddIndex == count - 1) {
                        if (itemIndex == itemAddIndex) {
                            // 맨 마지막 메세지일때
                            itemList.set(itemAddIndex, target);
                        } else {
                            if (itemIndex >= 0) {
                                itemList.remove(itemIndex);
                                itemList.add(target);
                            } else {
                                if (itemIndex == -1 && sendFailMessageCount == 0) {
                                    itemList.add(itemAddIndex + 1, target);
                                } else {
                                    itemList.add(itemAddIndex, target);
                                }
                            }
                        }
                    } else {
                        if (itemIndex >= 0) {
                            itemList.remove(itemIndex);
                        }
                        itemList.add(itemAddIndex, target);
                    }
                }
            }
        }
    }

    public void changeListItem(ChattingItem target) {
        if (target == null) {
            return;
        }

        int count = getItemCount();
        String targetItemKey = target.getTimeStampKey();
        for (int i = count - 1; i >= 0; i--) {
            ChattingItem item = itemList.get(i);
            String itemKey = item.getTimeStampKey();

            if (targetItemKey.equals(itemKey)) {
                if (target.getChatItem().isDeletedMessage()) {
                    itemList.remove(i);
                    notifyItemRemoved(i);
                } else {
                    itemList.set(i, target);
                    notifyItemChanged(i);
                }
                break;
            }
        }
    }

    public void onItemRemoved(ChattingItem target) {
        if (target == null) {
            return;
        }

        int count = getItemCount();
        String targetItemKey = target.getTimeStampKey();
        for (int i = count - 1; i >= 0; i--) {
            ChattingItem item = itemList.get(i);
            String itemKey = item.getTimeStampKey();

            if (targetItemKey.equals(itemKey)) {
                itemList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout dateLineLayout;
        TextView dateTextView;

        FrameLayout receiveMessageLayout;
        CircleImageView otherProfileImageView;

        TextView userNickNameTextView;
        TextView receiveMessageItem;
        TextView receiveTimeTextView;

        LinearLayout sendMessageLayout;
        TextView sendMessageItem;
        TextView sendTimeTextView;

        ImageView sendFailImageView;
        TextView sendFailTempTextView;
        TextView sendFailTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            dateLineLayout = itemView.findViewById(R.id.layout_date_line);
            dateTextView = itemView.findViewById(R.id.textview_date);

            receiveMessageLayout = itemView.findViewById(R.id.layout_receive_message);
            otherProfileImageView = itemView.findViewById(R.id.imageview_profile);
            userNickNameTextView = itemView.findViewById(R.id.textview_nickname);
            receiveMessageItem = itemView.findViewById(R.id.textview_receive_message);
            receiveTimeTextView = itemView.findViewById(R.id.textview_receive_message_time);

            sendMessageLayout = itemView.findViewById(R.id.layout_send_message);
            sendMessageItem = itemView.findViewById(R.id.textview_my_message);
            sendTimeTextView = itemView.findViewById(R.id.textview_send_message_time);

            sendFailImageView = itemView.findViewById(R.id.imageview_send_fail);
            sendFailTempTextView = itemView.findViewById(R.id.textview_send_fail_temp);
            sendFailTextView = itemView.findViewById(R.id.textview_send_fail);
        }
    }

}
