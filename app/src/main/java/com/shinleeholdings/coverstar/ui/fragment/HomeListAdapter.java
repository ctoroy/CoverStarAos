package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;
import com.shinleeholdings.coverstar.ui.custom.HomeEventPagerAdapter;

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
            setEventLayout((ContestNoticeItemViewHolder) holder);
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

    private void setEventLayout(ContestNoticeItemViewHolder viewHolder) {
        // TODO 아답터 및 데이터 설정

        HomeEventPagerAdapter adapter = new HomeEventPagerAdapter();
        viewHolder.viewPager.setAdapter(adapter);
        adapter.setData(mContestNoticeList, new HomeEventPagerAdapter.IPageMoveEventListener() {
            @Override
            public void onMove(int position) {
                viewHolder.viewPager.setCurrentItem(position, true);
            }
        });
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
        ViewPager viewPager;

        public ContestNoticeItemViewHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView;
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
