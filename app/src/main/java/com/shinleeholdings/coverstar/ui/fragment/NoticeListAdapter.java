package com.shinleeholdings.coverstar.ui.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.util.ImageLoader;

import java.util.ArrayList;

import network.model.NoticeItem;

public class NoticeListAdapter extends RecyclerView.Adapter {

    private final ArrayList<NoticeItem> itemList = new ArrayList<>();
    int selectedItemIndex = -1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.notice_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NoticeItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        viewHolder.noticeTitleTextView.setText(item.title);
        viewHolder.arrowImageView.setSelected(item.isSelected);
        if (item.isSelected) {
            viewHolder.contentsLayout.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(item.thumbnail)) {
                viewHolder.noticeImageView.setVisibility(View.GONE);
            } else {
                viewHolder.noticeImageView.setVisibility(View.VISIBLE);
                ImageLoader.loadImage(viewHolder.noticeImageView, item.thumbnail);
            }
            viewHolder.noticeContentsTextView.setText(item.content);
        } else {
            viewHolder.contentsLayout.setVisibility(View.GONE);
        }
    }

    public void setData(ArrayList<NoticeItem> dataList) {
        itemList.clear();
        selectedItemIndex = -1;

        if (dataList != null && dataList.size() > 0) {
            itemList.addAll(dataList);
        }

        notifyDataSetChanged();
    }

    public void clear() {
        setData(null);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    RecyclerView mRecyclerView;

    private class ItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView noticeTitleTextView;
        ImageView arrowImageView;

        LinearLayout contentsLayout;
        ImageView noticeImageView;
        TextView noticeContentsTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            LinearLayout noticeTitleLayout = itemView.findViewById(R.id.noticeTitleLayout);
            noticeTitleLayout.setOnClickListener(this);

            noticeTitleTextView = itemView.findViewById(R.id.noticeTitleTextView);
            arrowImageView = itemView.findViewById(R.id.arrowImageView);

            contentsLayout = itemView.findViewById(R.id.contentsLayout);
            noticeImageView = itemView.findViewById(R.id.noticeImageView);
            noticeContentsTextView = itemView.findViewById(R.id.noticeContentsTextView);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            NoticeItem item = itemList.get(position);
            if (item == null) {
                return;
            }

            if (item.isSelected) {
                item.isSelected = false;
                selectedItemIndex = -1;
                notifyItemChanged(position);
            } else {
                if (selectedItemIndex != -1) {
                    NoticeItem selectedItem = itemList.get(selectedItemIndex);
                    selectedItem.isSelected = false;
                    notifyItemChanged(selectedItemIndex);
                }

                item.isSelected = true;
                selectedItemIndex = position;
                notifyItemChanged(position);
                if (mRecyclerView != null) {
                    mRecyclerView.scrollToPosition(position);
                }

            }
        }
    }
}
