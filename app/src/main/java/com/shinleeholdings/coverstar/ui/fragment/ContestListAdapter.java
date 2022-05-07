package com.shinleeholdings.coverstar.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;

import java.util.ArrayList;

public class ContestListAdapter extends RecyclerView.Adapter {

    private MainActivity mMainActivity;

    private final ArrayList<ContestData> itemList = new ArrayList<>();

    public ContestListAdapter(MainActivity activity) {
        mMainActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.contest_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ContestData item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.contestItemLayout.setData(mMainActivity, item);
    }

    public void setData(ArrayList<ContestData> dataList) {
        itemList.clear();
        if (dataList != null && dataList.size() > 0) {
            itemList.addAll(dataList);
        }

        notifyDataSetChanged();
    }

    public void clear() {
        setData(null);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ContestItemLayout contestItemLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contestItemLayout = itemView.findViewById(R.id.contestItemLayout);
        }
    }
}
