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
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.Util;

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

        ImageLoader.loadImage(viewHolder.userImageView, item.userImagePath);
        viewHolder.userNicknameTextView.setText(item.userNickName);
        viewHolder.commentTimeTextView.setText(Util.changeFormattedDate(item.commentDate, CommentHelper.COMMENT_TIME_FORMAT));
        viewHolder.commentTextView.setText(item.comment);

        viewHolder.likeCountTextView.setText(item.getLikeCount() + "");
        viewHolder.unLikeCountTextView.setText(item.getUnLikeCount() + "");
        viewHolder.commentCountTextView.setText(item.getCommentCount() + "");

        // TODO 좋아요 싫어요 이미 했을때 아이콘 적용 필요
        viewHolder.likeLayout.setSelected(item.alreadyLike());
        viewHolder.unLikeLayout.setSelected(item.alreadyUnLike());

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

        LinearLayout likeLayout;
        TextView likeCountTextView;

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
            likeLayout = itemView.findViewById(R.id.recommendLayout);
            likeCountTextView = itemView.findViewById(R.id.recommendCountTextView);
            unLikeLayout = itemView.findViewById(R.id.unLikeLayout);
            unLikeCountTextView = itemView.findViewById(R.id.unLikeCountTextView);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);

            listIconImageView.setOnClickListener(this);
            likeLayout.setOnClickListener(this);
            unLikeLayout.setOnClickListener(this);
            commentLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            int position = getBindingAdapterPosition();
            CommentItem item = itemList.get(position);
            if (viewId == R.id.listIconImageView) {
                // TODO 메뉴
                if (item.isMyContestComment()) {
//                영상 올린사람은 – 고정, 삭제, 신고
                } else if (item.isMyComment()) {
//                댓글 올린 사람은 - 삭제
                } else {
//                일반 유저는 – 신고
                }
            } else if (viewId == R.id.recommendLayout){
                // TODO 추천
                if (item.alreadyLike()) {
                    // 좋아요 취소
                } else {
                    if (item.alreadyUnLike()) {
                        //  비추천 취소
                    }
                    // 좋아요
                }
            } else if (viewId == R.id.unLikeLayout){
                // TODO 비 추천
                if (item.alreadyUnLike()) {
                    // 비추천 취소
                } else {
                    if (item.alreadyLike()) {
                        // 좋아요 취소
                    }
                    // 비추천
                }
            } else if (viewId == R.id.commentLayout){
                // TODO 코멘트 클릭
            }
        }
    }
}
