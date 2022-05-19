package com.shinleeholdings.coverstar.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.ui.custom.CommentItemLayout;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;
    private String mCastCode;

    private final ArrayList<CommentItem> itemList = new ArrayList<>();

    public interface ICommentListEventListener {
        void onWriteClicked();
        void onCommentClicked(CommentItem item);
    }

    private ICommentListEventListener commentEventListener;

    private final int ITEM_TYPE_COMMENT_WRITE = 1;
    private final int ITEM_TYPE_COMMENT = 2;

    private final int headerCount = 1;

    public CommentListAdapter(MainActivity activity, String castCode, ICommentListEventListener clickListener) {
        mMainActivity = activity;
        mCastCode = castCode;
        commentEventListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_COMMENT_WRITE;
        }

        return ITEM_TYPE_COMMENT;
    }

    @Override
    public int getItemCount() {
        return itemList.size() + headerCount; // header +
    }

    public int getCommentItemCount() {
        return itemList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_COMMENT_WRITE) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_write, parent, false);
            return new WriteCommentViewHolder(view);
        } else {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_comment, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof WriteCommentViewHolder) {
            return;
        }

        final CommentItem item = itemList.get(position - headerCount);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.commentItemLayout.setData(mMainActivity, mCastCode, item, commentEventListener);
    }

    public void setData(ArrayList<CommentItem> dataList) {
        itemList.clear();
        if (dataList != null && dataList.size() > 0) {
            itemList.addAll(dataList);
        }

        notifyDataSetChanged();
    }

    public void addComment(CommentItem newItem) {

        boolean hasItem = false;

        for (int i=0; i <itemList.size(); i++) {
            CommentItem item = itemList.get(i);
            if (item.id.equals(newItem.id)) {
                hasItem = true;
                break;
            }
        }

        if (hasItem == false) {
            itemList.add(0, newItem);
            notifyItemInserted(0);
        }
    }

    public void changeComment(CommentItem newItem) {
        for (int i=0; i <itemList.size(); i++) {
            CommentItem item = itemList.get(i);
            if (item.id.equals(newItem.id)) {
                itemList.set(i, newItem);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeComment(String id) {
        for (int i=0; i <itemList.size(); i++) {
            CommentItem item = itemList.get(i);
            if (item.id.equals(id)) {
                itemList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void clear() {
        setData(null);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    private class WriteCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView writeTextView;
        public WriteCommentViewHolder(View itemView) {
            super(itemView);
            writeTextView = itemView.findViewById(R.id.writeTextView);
            writeTextView.setText(R.string.write_comment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            commentEventListener.onWriteClicked();
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        CommentItemLayout commentItemLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            commentItemLayout = (CommentItemLayout) itemView;
        }
    }
}
