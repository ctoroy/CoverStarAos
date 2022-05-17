package com.shinleeholdings.coverstar.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.ui.dialog.CommentEditFilterDialog;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;
    private String mCastCode;

    private final ArrayList<CommentItem> itemList = new ArrayList<>();

    public CommentListAdapter(MainActivity activity, String castCode) {
        mMainActivity = activity;
        mCastCode = castCode;
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
                CommentEditFilterDialog dialog = new CommentEditFilterDialog(mMainActivity);
                dialog.init(item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int viewId = view.getId();
                        if (NetworkHelper.isNetworkConnected() == false) {
                            Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (viewId == R.id.deleteLayout) {
                            CommentHelper.getSingleInstance().deleteCommentItem(mCastCode, item);
                            itemList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(MyApplication.getContext(), R.string.delete_done, Toast.LENGTH_SHORT).show();
                        } else if (viewId ==R.id.reportLayout) {
                            if (item.addReport()) {
                                HashMap<String, Object> valueMap = new HashMap<>();
                                valueMap.put(CommentHelper.FIELDNAME_REPORTS, item.reports);
                                CommentHelper.getSingleInstance().updateCommentItem(mCastCode, item, valueMap);
                            }
                            Toast.makeText(MyApplication.getContext(), R.string.report_done, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            } else if (viewId == R.id.recommendLayout){
                if (NetworkHelper.isNetworkConnected() == false) {
                    Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> valueMap = new HashMap<>();
                if (item.alreadyLike()) {
                    // 좋아요 취소
                    item.removeLike();
                    valueMap.put(CommentHelper.FIELDNAME_LIKES, item.likes);
                } else {
                    if (item.alreadyUnLike()) {
                        //  비추천 취소
                        item.removeUnLike();
                        valueMap.put(CommentHelper.FIELDNAME_UNLIKES, item.unLikes);
                    }
                    // 좋아요
                    item.addLike();
                    valueMap.put(CommentHelper.FIELDNAME_LIKES, item.likes);
                }

                CommentHelper.getSingleInstance().updateCommentItem(mCastCode, item, valueMap);
                notifyItemChanged(position);
            } else if (viewId == R.id.unLikeLayout){
                if (NetworkHelper.isNetworkConnected() == false) {
                    Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> valueMap = new HashMap<>();
                if (item.alreadyUnLike()) {
                    // 비추천 취소
                    item.removeUnLike();
                    valueMap.put(CommentHelper.FIELDNAME_UNLIKES, item.unLikes);
                } else {
                    if (item.alreadyLike()) {
                        // 좋아요 취소
                        item.removeLike();
                        valueMap.put(CommentHelper.FIELDNAME_LIKES, item.likes);
                    }
                    // 비추천
                    item.addUnLike();
                    valueMap.put(CommentHelper.FIELDNAME_UNLIKES, item.unLikes);
                }

                CommentHelper.getSingleInstance().updateCommentItem(mCastCode, item, valueMap);
                notifyItemChanged(position);
            } else if (viewId == R.id.commentLayout){
                // TODO 코멘트 클릭
            }
        }
    }
}
