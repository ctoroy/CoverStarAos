package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.ImageLoader;

import java.util.ArrayList;

public class HomeListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final MainActivity mMainActivity;
    private final HomeFragment.IPageMoveEventListener moveEventListener;

    private boolean mHasEventTab;
    private boolean mIsCoverStarTab;

    private ContestData contestRegistItem = null;
    private final ArrayList<ContestData> mItemList = new ArrayList<>();

    private final int ITEM_TYPE_CONTEST_NOTI = 1;
    private final int ITEM_TYPE_CONTEST = 2;

    public HomeListAdapter(MainActivity activity, HomeFragment.IPageMoveEventListener listener) {
        mMainActivity = activity;
        moveEventListener = listener;
        ContestManager.getSingleInstance().addInfoChangeListener(new ContestManager.IContestInfoUpdateListener() {
            @Override
            public void onWatchCountUpdated(ContestData item) {
                updateInfo(item);
            }

            @Override
            public void onVoteCountUpdated(ContestData item) {
                updateInfo(item);
            }
        });
    }

    private void updateInfo(ContestData targetItem) {
        try {
            for (int i=0; i< mItemList.size(); i++) {
                ContestData item = mItemList.get(i);
                if (item.castCode.equals(targetItem.castCode)) {
                    item.watchCnt = targetItem.watchCnt;
                    item.likes = targetItem.likes;
                    if (hasRegistItem()) {
                        notifyItemChanged(i + 1);
                    } else {
                        notifyItemChanged(i);
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasRegistItem() && position == 0) {
            return ITEM_TYPE_CONTEST_NOTI;
        }

        return ITEM_TYPE_CONTEST;
    }

    @Override
    public int getItemCount() {
        int itemListSize = mItemList.size();

        if (hasRegistItem()) {
            itemListSize = itemListSize + 1;
        }

        return itemListSize;
    }

    private boolean hasRegistItem() {
        return contestRegistItem != null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTEST_NOTI) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.home_notice_item, parent, false);
            return new ContestRegistItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.home_contest_list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContestRegistItemViewHolder) {
            setEventLayout((ContestRegistItemViewHolder) holder);
        } else {
            int headerCount = hasRegistItem() ? 1: 0;
            final ContestData item = mItemList.get(position - headerCount);
            if (item == null) {
                return;
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.contestItemLayout.setData(mMainActivity, item);
        }
    }

    private void setEventLayout(ContestRegistItemViewHolder viewHolder) {
        if (mIsCoverStarTab) {
//            viewHolder.titleTextView.setText(contestRegistItem.castTitle); // 경연참가 타이틀
            viewHolder.homeNoticeBgImageView.setImageResource(R.drawable.visual_bg1);
            viewHolder.topCoverStarImageView.setVisibility(View.VISIBLE);
            viewHolder.topDanceStarImageView.setVisibility(View.INVISIBLE);
            viewHolder.homeLeftTriangleView.setVisibility(View.VISIBLE);
            viewHolder.homeRightTriangleView.setVisibility(View.GONE);
        } else {
//            viewHolder.titleTextView.setText(contestRegistItem.sortSmall); // 이벤트 타이틀
            viewHolder.homeNoticeBgImageView.setImageResource(R.drawable.visual_bg2);
            viewHolder.topDanceStarImageView.setVisibility(View.VISIBLE);
            viewHolder.topCoverStarImageView.setVisibility(View.INVISIBLE);
            viewHolder.homeRightTriangleView.setVisibility(View.VISIBLE);
            viewHolder.homeLeftTriangleView.setVisibility(View.GONE);
        }
        ImageLoader.loadImage(viewHolder.homeNoticeMainImageView, contestRegistItem.product);

        viewHolder.registTextView.setOnClickListener(view -> mMainActivity.registContest(contestRegistItem));
    }

    public void setData(ContestData registItem, ArrayList<ContestData> contestList, boolean isCoverStarTab, boolean hasEventTab) {
        mItemList.clear();
        mIsCoverStarTab = isCoverStarTab;
        mHasEventTab = hasEventTab;

        contestRegistItem = registItem;

        if (contestList != null && contestList.size() > 0) {
            mItemList.addAll(contestList);
        }

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    private class ContestRegistItemViewHolder extends RecyclerView.ViewHolder {
        ImageView homeNoticeBgImageView;
        ImageView homeNoticeMainImageView;
        ImageView topCoverStarImageView;
        ImageView topDanceStarImageView;

        TextView registTextView;

        View homeLeftTriangleView;
        View homeRightTriangleView;

        public ContestRegistItemViewHolder(View itemView) {
            super(itemView);
            homeNoticeBgImageView = itemView.findViewById(R.id.homeNoticeBgImageView);
            homeNoticeMainImageView = itemView.findViewById(R.id.homeNoticeMainImageView);
            topCoverStarImageView = itemView.findViewById(R.id.topCoverStarImageView);
            topDanceStarImageView = itemView.findViewById(R.id.topDanceStarImageView);

            homeLeftTriangleView = itemView.findViewById(R.id.homeLeftTriangleView);
            homeRightTriangleView = itemView.findViewById(R.id.homeRightTriangleView);

            registTextView = itemView.findViewById(R.id.registTextView);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        ContestItemLayout contestItemLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contestItemLayout = itemView.findViewById(R.id.contestItemLayout);
            contestItemLayout.updateLayout(mMainActivity);
        }
    }
}
