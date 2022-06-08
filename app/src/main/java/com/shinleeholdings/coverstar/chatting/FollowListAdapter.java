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

import java.util.ArrayList;

import network.model.CoverStarUser;

public class FollowListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;

    private ArrayList<CoverStarUser> itemList = new ArrayList<>();

    public FollowListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.follow_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CoverStarUser item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        ImageLoader.loadUserImage(viewHolder.userImageView, item.userProfileImage);
        viewHolder.userNameTextView.setText(item.nickName);
    }

    public void setData(ArrayList<CoverStarUser> dataList) {
        itemList.clear();
        if (dataList != null && dataList.size() > 0) {
            itemList.addAll(dataList);
        }

        notifyDataSetChanged();
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
        TextView userNameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            userImageView = itemView.findViewById(R.id.userImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }

        @Override
        public void onClick(View view) {

            int position = getBindingAdapterPosition();
            CoverStarUser user = itemList.get(position);
            if (user == null) {
                return;
            }

            ChatRoomListHelper.getSingleInstance().startChat(mMainActivity, user, null);
        }
    }
}
