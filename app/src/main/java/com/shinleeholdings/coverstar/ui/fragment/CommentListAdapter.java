package com.shinleeholdings.coverstar.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.ui.custom.CommentItemLayout;
import com.shinleeholdings.coverstar.ui.dialog.CommentEditFilterDialog;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.NetworkHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;
    private String mCastCode;

    private final ArrayList<CommentItem> itemList = new ArrayList<>();

    public interface ICommentClickListener {
        public void onCommentClicked(CommentItem item);
    }

    private ICommentClickListener commentClickListener;

    public CommentListAdapter(MainActivity activity, String castCode, ICommentClickListener clickListener) {
        mMainActivity = activity;
        mCastCode = castCode;
        commentClickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final CommentItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.commentItemLayout.setData(mMainActivity, item, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewId = view.getId();
                int position = holder.getBindingAdapterPosition();
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
                } else if (viewId == R.id.commentLayout || viewId == R.id.commentInfoLayout){
                    commentClickListener.onCommentClicked(item);
                }
            }
        });
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

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        CommentItemLayout commentItemLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            commentItemLayout = (CommentItemLayout) itemView;
        }
    }
}
