package com.shinleeholdings.coverstar.chatting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.ui.CircleImageView;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

public class ChatRoomListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;

    private ArrayList<ChatRoomItem> itemList = new ArrayList<>();

    public ChatRoomListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.chatroom_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChatRoomItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        ImageLoader.loadUserImage(viewHolder.userImageView, item.getChattingMemberPhoto());
        viewHolder.roomNameTextView.setText(item.getDisplayRoomName());
        viewHolder.messageTextView.setText(item.getLastMessage());
        viewHolder.timeTextView.setText(Util.getMessageTimeStringValue(item.getMessageDate()));

        // 채팅방 뱃지 표시하려면 이 값 사용
        long chattingRoomBadgeCount = item.getBadgeCount();
    }

    public void setData(ArrayList<ChatRoomItem> dataList) {
        itemList = dataList;
        notifyDataSetChanged();
    }

    public void notifyDataChanged(String chatId) {
        for (int i = 0; i < itemList.size(); i++) {
            ChatRoomItem item = itemList.get(i);
            if (item.getChatId().equals(chatId)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView userImageView;
        TextView roomNameTextView;
        TextView messageTextView;
        TextView timeTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            userImageView = itemView.findViewById(R.id.userImageView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.messageDateTextView);
        }

        @Override
        public void onClick(View view) {

            int position = getBindingAdapterPosition();
            ChatRoomItem info = itemList.get(position);
            if (info == null) {
                return;
            }

            ChatRoomListHelper.getSingleInstance().startChatActivity(mMainActivity, info.getChatId());
        }
    }
}
