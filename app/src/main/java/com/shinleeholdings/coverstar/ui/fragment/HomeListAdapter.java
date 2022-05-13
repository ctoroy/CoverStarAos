package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
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
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;

import java.util.ArrayList;

public class HomeListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private MainActivity mMainActivity;

    private boolean mHasEventTab;
    private boolean mIsCoverStarTab;

    private ContestData contestRegistItem = null;
    private final ArrayList<ContestData> mItemList = new ArrayList<>();

    private final int ITEM_TYPE_CONTEST_NOTI = 1;
    private final int ITEM_TYPE_CONTEST = 2;

    private HomePagerAdapter.IPageMoveEventListener moveEventListener;

    public HomeListAdapter(MainActivity activity, HomePagerAdapter.IPageMoveEventListener listener) {
        mMainActivity = activity;
        moveEventListener = listener;
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
            viewHolder.homeNoticeBgImageView.setImageResource(R.drawable.visual_bg1);
            viewHolder.homeLeftTitleSelectedTextView.setVisibility(View.VISIBLE);
            viewHolder.homeLeftTriangleView.setVisibility(View.VISIBLE);
            viewHolder.homeRightTitleSelectedTextView.setVisibility(View.GONE);
            viewHolder.homeLeftTitleUnSelectedLayout.setVisibility(View.GONE);
            viewHolder.homeRightTriangleView.setVisibility(View.GONE);
            if (mHasEventTab) {
                viewHolder.homeRightTitleUnSelectedLayout.setVisibility(View.VISIBLE);
                viewHolder.homeRightTitleUnSelectedLayout.setOnClickListener(view -> moveEventListener.onPageMove(1));
            } else {
                viewHolder.homeRightTitleUnSelectedLayout.setVisibility(View.GONE);
            }
        } else {
            viewHolder.homeNoticeBgImageView.setImageResource(R.drawable.visual_bg2);
            viewHolder.homeRightTitleSelectedTextView.setVisibility(View.VISIBLE);
            viewHolder.homeLeftTitleUnSelectedLayout.setVisibility(View.VISIBLE);
            viewHolder.homeRightTriangleView.setVisibility(View.VISIBLE);
            viewHolder.homeLeftTitleUnSelectedLayout.setOnClickListener(view -> moveEventListener.onPageMove(0));
            viewHolder.homeLeftTriangleView.setVisibility(View.GONE);
            viewHolder.homeLeftTitleSelectedTextView.setVisibility(View.GONE);
            viewHolder.homeRightTitleUnSelectedLayout.setVisibility(View.GONE);
        }

        // TODO 데이터 세팅 : contestRegistItem
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
        TextView homeLeftTitleSelectedTextView;
        LinearLayout homeRightTitleUnSelectedLayout;
        TextView homeRightTitleSelectedTextView;
        LinearLayout homeLeftTitleUnSelectedLayout;

        TextView registTextView;

        View homeLeftTriangleView;
        View homeRightTriangleView;

        public ContestRegistItemViewHolder(View itemView) {
            super(itemView);
            homeNoticeBgImageView = itemView.findViewById(R.id.homeNoticeBgImageView);
            homeLeftTitleSelectedTextView = itemView.findViewById(R.id.homeLeftTitleSelectedTextView);
            homeRightTitleUnSelectedLayout = itemView.findViewById(R.id.homeRightTitleUnSelectedLayout);
            homeRightTitleSelectedTextView = itemView.findViewById(R.id.homeRightTitleSelectedTextView);
            homeLeftTitleUnSelectedLayout = itemView.findViewById(R.id.homeLeftTitleUnSelectedLayout);

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
        }
    }
}
