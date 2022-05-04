package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.data.ContestNotice;
import com.shinleeholdings.coverstar.util.ContestManager;

import java.util.ArrayList;

public class HomeListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    ContestNotice mContestNotice;
    private final ArrayList<ContestData> mItemList = new ArrayList<>();
    private MainActivity mMainActivity;

    private final int ITEM_TYPE_NOTICE = 1;
    private final int ITEM_TYPE_CONTEST = 2;

    public HomeListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (mContestNotice != null && position == 0) {
            return ITEM_TYPE_NOTICE;
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
        if (viewType == ITEM_TYPE_NOTICE) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.home_notice_item, parent, false);
            return new NoticeItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.contest_list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NoticeItemViewHolder) {
            // TODO 공지 세팅
        } else {
            final ContestData item = mItemList.get(position);
            if (item == null) {
                return;
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            // TODO 콘테스트 데이터 세팅
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

    private class NoticeItemViewHolder extends RecyclerView.ViewHolder {
        // TODO 공지뷰 세팅

        public NoticeItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        ImageView contestImageView;

        TextView likeCountTextView;
        TextView songTitleTextView;
        TextView singerNameTextView;
        TextView originalSingerNameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contestImageView = itemView.findViewById(R.id.contestImageView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            songTitleTextView = itemView.findViewById(R.id.songTitleTextView);
            singerNameTextView = itemView.findViewById(R.id.singerNameTextView);
            originalSingerNameTextView = itemView.findViewById(R.id.originalSingerNameTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ContestData info = mItemList.get(position);
            if (info == null) {
                return;
            }
            ContestManager.getSingleInstance().showContestDetailFragment(mMainActivity, info);
        }
    }
}
