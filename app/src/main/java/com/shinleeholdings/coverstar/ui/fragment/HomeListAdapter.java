package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;

import java.util.ArrayList;

public class HomeListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final ArrayList<ContestData> mContestNoticeList = new ArrayList<>();
    private final ArrayList<ContestData> mItemList = new ArrayList<>();
    private MainActivity mMainActivity;

    private final int ITEM_TYPE_CONTEST_NOTI = 1;
    private final int ITEM_TYPE_CONTEST = 2;

    public HomeListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeaderContest() && position == 0) {
            return ITEM_TYPE_CONTEST_NOTI;
        }

        return ITEM_TYPE_CONTEST;
    }

    @Override
    public int getItemCount() {
        int itemListSize = mItemList.size();
        if (itemListSize > 0) {
            if (hasHeaderContest()) {
                itemListSize = itemListSize + 1;
            }
        }
        return itemListSize;
    }

    private boolean hasHeaderContest() {
        return mContestNoticeList.size() > 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTEST_NOTI) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.home_notice_item, parent, false);
            return new ContestNoticeItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.home_contest_list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContestNoticeItemViewHolder) {
            // TODO 공지 세팅
        } else {
            int headerCount = hasHeaderContest() ? 1: 0;
            final ContestData item = mItemList.get(position - headerCount);
            if (item == null) {
                return;
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.contestItemLayout.setData(mMainActivity, item);
        }
    }

    public void setData(ArrayList<ContestData> contestList, ArrayList<ContestData> dataList) {
        mItemList.clear();
        mContestNoticeList.clear();

        if (dataList != null && dataList.size() > 0) {
            mContestNoticeList.addAll(contestList);
            mItemList.addAll(dataList);
        }

        notifyDataSetChanged();
    }

    public void clear() {
        setData(null, null);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    private class ContestNoticeItemViewHolder extends RecyclerView.ViewHolder {
        // TODO 공지뷰 세팅
        ViewPager2 viewPager;

        public ContestNoticeItemViewHolder(View itemView) {
            super(itemView);
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
