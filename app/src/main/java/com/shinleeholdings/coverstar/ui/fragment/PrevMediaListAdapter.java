package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

public class PrevMediaListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    public SortFilterDialog.SortType mSelectedSortType = SortFilterDialog.SortType.LATEST;

    private final ArrayList<ContestData> mEpilogueList = new ArrayList<>();
    private final ArrayList<ContestData> mContestMediaList = new ArrayList<>();
    private MainActivity mMainActivity;

    public static final int ITEM_TYPE_EPILOGUE = 1;
    public static final int ITEM_TYPE_MEDIA_HEADER = 2;
    public final int ITEM_TYPE_CONTEST_MEDIA = 3;

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
        if (mEpilogueList.size() > 0) {
            if (position == 0) {
                return ITEM_TYPE_EPILOGUE;
            } else {
                if (mContestMediaList.size() > 0 && position == 1) {
                    return ITEM_TYPE_MEDIA_HEADER;
                }
            }
        } else {
            if (mContestMediaList.size() > 0 && position == 0) {
                return ITEM_TYPE_MEDIA_HEADER;
            }
        }

        return ITEM_TYPE_CONTEST_MEDIA;
    }

    @Override
    public int getItemCount() {
        int itemCount = mContestMediaList.size();
        if (mEpilogueList.size() > 0) {
            itemCount = itemCount + 1;
        }
        if (mContestMediaList.size() > 0) {
            itemCount = itemCount + 1;
        }

        return itemCount;
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
//            for (int i =0; i < mEpilogueList.size(); i++) {
//                ContestItemLayout contest = new ContestItemLayout(mMainActivity);
//                contest.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPixel(mMainActivity, 300f), LinearLayout.LayoutParams.WRAP_CONTENT));
//                contest.setData(mMainActivity, mEpilogueList.get(i));
//                viewHolder.horizontalScrollView.addView(contest);
//
//                if (i < mEpilogueList.size() - 1) {
//                    View divider = new View(mMainActivity);
//                    divider.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPixel(mMainActivity, 20f), 1));
//                    viewHolder.horizontalScrollView.addView(divider);
//                }
//            }
        } else if (holder instanceof ContestHeaderItemViewHolder) {
            ContestHeaderItemViewHolder viewHolder = (ContestHeaderItemViewHolder) holder;
            switch (mSelectedSortType) {
                case POPULAR:
                    viewHolder.selectedFilterTextView.setText(mMainActivity.getString(R.string.order_popular));
                    break;
                case SEARCH:
                    viewHolder.selectedFilterTextView.setText(mMainActivity.getString(R.string.order_search));
                    break;
                case LATEST:
                    viewHolder.selectedFilterTextView.setText(mMainActivity.getString(R.string.order_recently));
                    break;
            }
        } else {
            int headerCount = 1;
            if (mEpilogueList.size() > 0) {
                headerCount = 2;
            }

            final ContestData item = mContestMediaList.get(position - headerCount);
            if (item == null) {
                return;
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            ImageLoader.loadImage(viewHolder.contestImageView, item.getBgImagePath());
            viewHolder.songTitleTextView.setText(item.getTitle());
            viewHolder.singerNameTextView.setText(item.getNickName());
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
            SortFilterDialog dialog = new SortFilterDialog(mMainActivity);
            dialog.init(mSelectedSortType, new SortFilterDialog.ISortTypeSelectListener() {
                @Override
                public void onSortTypeSelected(SortFilterDialog.SortType type) {
                    if (mSelectedSortType == type) {
                        return;
                    }
                    mSelectedSortType = type;
                    switch (mSelectedSortType) {
                        case POPULAR:
                            selectedFilterTextView.setText(mMainActivity.getString(R.string.order_popular));
                            break;
                        case SEARCH:
                            selectedFilterTextView.setText(mMainActivity.getString(R.string.order_search));
                            break;
                        case LATEST:
                            selectedFilterTextView.setText(mMainActivity.getString(R.string.order_recently));
                            break;
                    }
                    Util.sortList(type, mContestMediaList);
                    notifyDataSetChanged();
                }
            });
            dialog.show();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView contestImageView;
        TextView songTitleTextView;
        TextView singerNameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contestImageView = itemView.findViewById(R.id.contestImageView);

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) contestImageView.getLayoutParams();
            int sideMargin = Util.convertDimenResIdToPixel(mMainActivity, R.dimen.contest_item_side_margin) *2;
            int width = (Util.getDisplayWidth(mMainActivity) - sideMargin) / 2;
            param.height =  (3 * width) / 4;
            contestImageView.setLayoutParams(param);

            songTitleTextView = itemView.findViewById(R.id.songTitleTextView);
            singerNameTextView = itemView.findViewById(R.id.singerNameTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            ContestDetailFragment contestDetailFragment = new ContestDetailFragment();
            Bundle bundle = new Bundle();
            int headerCount = 1;
            if (mEpilogueList.size() > 0) {
                headerCount = 2;
            }
            bundle.putParcelable(AppConstants.EXTRA.CONTEST_DATA, mContestMediaList.get(position - headerCount));
            contestDetailFragment.setArguments(bundle);
            mMainActivity.onFragmentInteractionCallback(contestDetailFragment);
        }
    }
}
