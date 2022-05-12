package com.shinleeholdings.coverstar.ui.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;

import java.util.ArrayList;
import java.util.Collections;

import network.model.HomeContentsDataList;

public class HomePagerAdapter extends PagerAdapter {
    ArrayList<Pair<ContestData, ArrayList<ContestData>>> itemList = new ArrayList<>();
    private MainActivity mMainActivity;

    private IPageMoveEventListener moveEventListener;

    public interface IPageMoveEventListener {
        void onPageMove(int position);
    }

    public HomePagerAdapter(MainActivity activity, IPageMoveEventListener listener) {
        mMainActivity = activity;
        moveEventListener = listener;
    }

    public void updateSort(SortFilterDialog.SortType selectedSortType) {
        for (int i=0; i< itemList.size(); i++) {
            Pair<ContestData, ArrayList<ContestData>> data = itemList.get(i);
            if (data.second != null && data.second.size() > 0) {
                Collections.sort();
                // TODO 로컬 정렬
                // selectedSortType
                // 최신순 : castStartDate
                // 조회순 : watchCnt
                // 인기순 : episode
            }
        }
    }

    public void setData(HomeContentsDataList result) {
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        RecyclerView itemView = (RecyclerView) inflater.inflate(R.layout.layout_home_pager_item, container, false);
        itemView.setLayoutManager(new LinearLayoutManager(mMainActivity));
        boolean hasEventTab = itemList.size() > 1;
        boolean isCoverStarTab = position == 0;
        HomeListAdapter homeCoverStarListAdapter = new HomeListAdapter(mMainActivity, moveEventListener);
        itemView.setAdapter(homeCoverStarListAdapter);
        Pair<ContestData, ArrayList<ContestData>> data = itemList.get(position);
        homeCoverStarListAdapter.setData(data.first, data.second, isCoverStarTab, hasEventTab);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
