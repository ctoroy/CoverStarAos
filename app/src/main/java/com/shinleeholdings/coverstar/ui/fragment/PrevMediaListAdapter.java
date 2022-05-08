package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

public class PrevMediaListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final ArrayList<ContestData> mEpilogueList = new ArrayList<>();
    private final ArrayList<ContestData> mContestMediaList = new ArrayList<>();
    private MainActivity mMainActivity;

    public static final int ITEM_TYPE_EPILOGUE = 1;
    public static final int ITEM_TYPE_MEDIA_HEADER = 2;
    public final int ITEM_TYPE_CONTEST_MEDIA = 3;

    private static final int HEADER_COUNT = 2;

    public PrevMediaListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    public void setData(ArrayList<ContestData> epliogList, ArrayList<ContestData> contestList) {
        mEpilogueList.clear();
        mContestMediaList.clear();

        if (epliogList != null && epliogList.size() > 0) {
            mEpilogueList.addAll(epliogList);
        }

        if (contestList != null && contestList.size() > 0) {
            mContestMediaList.addAll(contestList);
        }

        notifyDataSetChanged();
    }

    public void clear() {
        setData(null, null);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_EPILOGUE;
        }

        if (position == 1) {
            return ITEM_TYPE_MEDIA_HEADER;
        }

        return ITEM_TYPE_CONTEST_MEDIA;
    }

    @Override
    public int getItemCount() {
        return mContestMediaList.size() + HEADER_COUNT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_EPILOGUE) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_epilogue, parent, false);
            return new EpilogueItemViewHolder(view);
        } else if (viewType == ITEM_TYPE_MEDIA_HEADER) {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_media_header, parent, false);
            return new ContestHeaderItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.layout_mini_contest_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof EpilogueItemViewHolder) {
            EpilogueItemViewHolder viewHolder = (EpilogueItemViewHolder) holder;
            for (int i =0; i < mEpilogueList.size(); i++) {
                ContestItemLayout contest = new ContestItemLayout(mMainActivity);
                contest.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPixel(mMainActivity, 300f), LinearLayout.LayoutParams.WRAP_CONTENT));
                contest.setData(mMainActivity, mEpilogueList.get(i));
                viewHolder.horizontalScrollView.addView(contest);

                if (i < mEpilogueList.size() - 1) {
                    View divider = new View(mMainActivity);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPixel(mMainActivity, 20f), 1));
                    viewHolder.horizontalScrollView.addView(divider);
                }
            }
        } else if (holder instanceof ContestHeaderItemViewHolder) {
            ContestHeaderItemViewHolder viewHolder = (ContestHeaderItemViewHolder) holder;
            // TODO 선택된 필터 텍스트 입력
            viewHolder.selectedFilterTextView.setText("");
        } else {
            final ContestData item = mContestMediaList.get(position - HEADER_COUNT);
            if (item == null) {
                return;
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            // TODO  이미지
            ImageLoader.loadImage(viewHolder.contestImageView, "");
            viewHolder.songTitleTextView.setText("e" + position);
            viewHolder.singerNameTextView.setText("e" + position);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    private class EpilogueItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout horizontalScrollView;

        public EpilogueItemViewHolder(View itemView) {
            super(itemView);
            horizontalScrollView = itemView.findViewById(R.id.contestHorizontalScrollView);
        }
    }

    private class ContestHeaderItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout filterLayout;
        TextView selectedFilterTextView;

        public ContestHeaderItemViewHolder(View itemView) {
            super(itemView);
            filterLayout = itemView.findViewById(R.id.filterLayout);
            filterLayout.setOnClickListener(this);
            selectedFilterTextView = itemView.findViewById(R.id.selectedFilterTextView);
        }

        @Override
        public void onClick(View view) {
            // TODO 경연영상 필터 클릭 이벤트
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView contestImageView;
        TextView songTitleTextView;
        TextView singerNameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contestImageView = itemView.findViewById(R.id.contestImageView);
            songTitleTextView = itemView.findViewById(R.id.songTitleTextView);
            singerNameTextView = itemView.findViewById(R.id.singerNameTextView);
        }
    }
}
