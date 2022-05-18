package com.shinleeholdings.coverstar.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ReplyItem;
import com.shinleeholdings.coverstar.ui.dialog.CommentEditFilterDialog;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class ReplyListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;

    private final ArrayList<ReplyItem> itemList = new ArrayList<>();
    private String mCastCode;

    public ReplyListAdapter(MainActivity activity, String castCode) {
        mMainActivity = activity;
        mCastCode = castCode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_reply, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ReplyItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        ImageLoader.loadImage(viewHolder.userImageView, item.userImagePath);
        viewHolder.userNicknameTextView.setText(item.userNickName);
        viewHolder.commentTimeTextView.setText(Util.changeFormattedDate(item.messageDate, CommentHelper.COMMENT_TIME_FORMAT));
        viewHolder.commentTextView.setText(item.message);

        viewHolder.likeCountTextView.setText(item.getLikeCount() + "");
        viewHolder.unLikeCountTextView.setText(item.getUnLikeCount() + "");

        viewHolder.likeLayout.setSelected(item.alreadyLike());
        viewHolder.unLikeLayout.setSelected(item.alreadyUnLike());
    }

    public void setData(ArrayList<ReplyItem> dataList) {
        itemList.clear();
        if (dataList != null && dataList.size() > 0) {
            itemList.addAll(dataList);
        }

        notifyDataSetChanged();
    }

    public void addReply(ReplyItem newItem) {

        boolean hasItem = false;

        for (int i=0; i <itemList.size(); i++) {
            ReplyItem item = itemList.get(i);
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

    public void changeReply(ReplyItem newItem) {
        for (int i=0; i <itemList.size(); i++) {
            ReplyItem item = itemList.get(i);
            if (item.id.equals(newItem.id)) {
                itemList.set(i, newItem);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeReply(String id) {
        for (int i=0; i <itemList.size(); i++) {
            ReplyItem item = itemList.get(i);
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            userNicknameTextView = itemView.findViewById(R.id.userNicknameTextView);
            commentTimeTextView = itemView.findViewById(R.id.commentTimeTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            listIconImageView = itemView.findViewById(R.id.replyListIconImageView);
            likeLayout = itemView.findViewById(R.id.replyRecommendLayout);
            likeCountTextView = itemView.findViewById(R.id.recommendCountTextView);
            unLikeLayout = itemView.findViewById(R.id.replyUnLikeLayout);
            unLikeCountTextView = itemView.findViewById(R.id.unLikeCountTextView);

            listIconImageView.setOnClickListener(this);
            likeLayout.setOnClickListener(this);
            unLikeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            int position = getBindingAdapterPosition();
            ReplyItem item = itemList.get(position);
            if (viewId == R.id.replyListIconImageView) {
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
                            ProgressDialogHelper.show(mMainActivity);
                            CommentHelper.getSingleInstance().deleteReplyItem(mCastCode, item, new CommentHelper.IFireStoreActionCompleteListener() {
                                @Override
                                public void onCompleted() {
                                    ProgressDialogHelper.dismiss();
                                    Toast.makeText(MyApplication.getContext(), R.string.delete_done, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (viewId ==R.id.reportLayout) {
                            if (item.addReport()) {
                                HashMap<String, Object> valueMap = new HashMap<>();
                                valueMap.put(CommentHelper.FIELDNAME_REPORTS, item.reports);
                                ProgressDialogHelper.show(mMainActivity);
                                CommentHelper.getSingleInstance().updateReplyItem(mCastCode, item, valueMap, new CommentHelper.IFireStoreActionCompleteListener() {
                                    @Override
                                    public void onCompleted() {
                                        ProgressDialogHelper.dismiss();
                                        Toast.makeText(MyApplication.getContext(), R.string.report_done, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
                dialog.show();
            } else if (viewId == R.id.replyRecommendLayout){
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

                ProgressDialogHelper.show(mMainActivity);
                CommentHelper.getSingleInstance().updateReplyItem(mCastCode, item, valueMap, () -> ProgressDialogHelper.dismiss());
            } else if (viewId == R.id.replyUnLikeLayout){
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

                ProgressDialogHelper.show(mMainActivity);
                CommentHelper.getSingleInstance().updateReplyItem(mCastCode, item, valueMap, () -> ProgressDialogHelper.dismiss());
            }
        }
    }
}
