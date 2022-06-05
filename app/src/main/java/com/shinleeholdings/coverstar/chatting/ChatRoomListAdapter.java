package com.shinleeholdings.coverstar.chatting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;

import java.util.ArrayList;

public class ChatRoomListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;

    private ArrayList<ChatRoomItem> itemList = new ArrayList<>();

    public ChatRoomListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.contest_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChatRoomItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        // TODO 디자인 적용
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
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
