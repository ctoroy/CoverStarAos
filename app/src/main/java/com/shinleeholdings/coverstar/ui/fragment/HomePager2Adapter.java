package com.shinleeholdings.coverstar.ui.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

import network.model.ContestDataList;

public class HomePager2Adapter extends RecyclerView.Adapter {
    ArrayList<Pair<ContestData, ArrayList<ContestData>>> itemList = new ArrayList<>();
    private MainActivity mMainActivity;

    private HomeFragment.IPageMoveEventListener moveEventListener;

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.layout_home_pager_item, parent, false);
        return new HomePagerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HomePagerItemViewHolder viewHolder = (HomePagerItemViewHolder) holder;
        boolean hasEventTab = itemList.size() > 1;
        boolean isCoverStarTab = position == 0;
        HomeListAdapter homeCoverStarListAdapter = new HomeListAdapter(mMainActivity, moveEventListener);
        viewHolder.recyclerView.setAdapter(homeCoverStarListAdapter);
        Pair<ContestData, ArrayList<ContestData>> data = itemList.get(position);
        homeCoverStarListAdapter.setData(data.first, data.second, isCoverStarTab, hasEventTab);
    }

    public HomePager2Adapter(MainActivity activity, HomeFragment.IPageMoveEventListener listener) {
        mMainActivity = activity;
        moveEventListener = listener;
    }

    public void updateSort(SortFilterDialog.SortType selectedSortType) {
        for (int i=0; i< itemList.size(); i++) {
            Pair<ContestData, ArrayList<ContestData>> data = itemList.get(i);
            Util.sortList(selectedSortType, data.second);
        }
    }

    public boolean updateCount(ContestData targetItem) {
        for (int i=0; i< itemList.size(); i++) {
            Pair<ContestData, ArrayList<ContestData>> data = itemList.get(i);
            for (int j=0; j <data.second.size(); j++) {
                ContestData item = data.second.get(j);
                if (item.castCode.equals(targetItem.castCode)) {
                    item.watchCnt = targetItem.watchCnt;
                    item.likes = targetItem.likes;
                    notifyItemChanged(i);
                    return true;
                }
            }
        }
        return false;
    }

    public void setData(ContestDataList result) {
        itemList.clear();

        ContestData coverStartRegistItem = null;
        ContestData eventRegistItem = null;

        ArrayList<ContestData> coverStartList = new ArrayList<>();
        ArrayList<ContestData> eventList = new ArrayList<>();

        for (int i=0; i<result.size(); i++) {
            ContestData item = result.get(i);
            if (item.category == 0) { // 커버스타 상단
                coverStartRegistItem = item;
            } else if (item.category == 1) { // 이벤트 상단
                eventRegistItem = item;
            } else if (item.category == 2){  // 경연 참가
                if (item.castType == 0) {  // 커버스타 참가
                    coverStartList.add(item);
                } else if (item.castType == 1) { // 이벤트 참가
                    eventList.add(item);
                }
            }
        }

        if (coverStartRegistItem != null || coverStartList.size() > 0) {
            itemList.add(new Pair<>(coverStartRegistItem, coverStartList));
        }

        if (eventRegistItem != null || eventList.size() > 0) {
            itemList.add(new Pair<>(eventRegistItem, eventList));
        }
    }

    private class HomePagerItemViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public HomePagerItemViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView)itemView;
            recyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity));
        }
    }
}
