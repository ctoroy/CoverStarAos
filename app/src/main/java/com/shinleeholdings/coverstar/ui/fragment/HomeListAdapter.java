package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.data.ContestNotice;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;

import java.util.ArrayList;

public class HomeListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    ContestNotice mContestNotice;
    private final ArrayList<ContestData> mItemList = new ArrayList<>();
    private MainActivity mMainActivity;

    private final int ITEM_TYPE_CONTEST_NOTI = 1;
    private final int ITEM_TYPE_CONTEST = 2;

    public HomeListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (mContestNotice != null && position == 0) {
            return ITEM_TYPE_CONTEST_NOTI;
        }

        return ITEM_TYPE_CONTEST;
    }

    @Override
    public int getItemCount() {
        int itemListSize = mItemList.size();
        if (itemListSize > 0) {
            if (mContestNotice != null) {
                itemListSize = itemListSize + 1;
            }
        }
        return itemListSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTEST_NOTI) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.home_notice_item, parent, false);
            return new ContestNoticeItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.contest_list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContestNoticeItemViewHolder) {
            // TODO 공지 세팅
        } else {
            final ContestData item = mItemList.get(position);
            if (item == null) {
                return;
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.contestItemLayout.setData(mMainActivity, item);
        }
    }

    public void setData(ContestNotice contestNotice, ArrayList<ContestData> dataList) {
        mItemList.clear();
        mContestNotice = null;

        if (dataList != null && dataList.size() > 0) {
            mContestNotice = contestNotice;
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
