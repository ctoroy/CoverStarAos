package com.shinleeholdings.coverstar.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;

    private final ArrayList<CommentItem> itemList = new ArrayList<>();

    public CommentListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CommentItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.userNicknameTextView.setText("" + position);
    }

    public void setData(ArrayList<CommentItem> dataList) {
        itemList.clear();
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

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        TextView userNicknameTextView;
        TextView commentTimeTextView;
        TextView commentTextView;
        ImageView listIconImageView;

        LinearLayout recommendLayout;
        TextView recommendCountTextView;

        LinearLayout unLikeLayout;
        TextView unLikeCountTextView;

        LinearLayout commentLayout;
        TextView commentCountTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.userImageView);
            userNicknameTextView = itemView.findViewById(R.id.userNicknameTextView);
            commentTimeTextView = itemView.findViewById(R.id.commentTimeTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            listIconImageView = itemView.findViewById(R.id.listIconImageView);
            recommendLayout = itemView.findViewById(R.id.recommendLayout);
            recommendCountTextView = itemView.findViewById(R.id.recommendCountTextView);
            unLikeLayout = itemView.findViewById(R.id.unLikeLayout);
            unLikeCountTextView = itemView.findViewById(R.id.unLikeCountTextView);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);

        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            int position = getBindingAdapterPosition();
            if (viewId == R.id.listIconImageView) {
                // TODO 메뉴
//                영상 올린사람은 – 고정, 삭제, 신고
//                댓글 올린 사람은 - 삭제
//                일반 유저는 – 신고
            } else if (viewId == R.id.recommendLayout){
                // TODO 추천
            } else if (viewId == R.id.unLikeLayout){
                // TODO 비 추천
            } else if (viewId == R.id.commentLayout){
                // TODO 코멘트 클릭
            }
        }
    }
}
