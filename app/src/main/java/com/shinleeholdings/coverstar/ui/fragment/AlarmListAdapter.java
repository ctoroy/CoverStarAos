package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.AlarmItem;
import com.shinleeholdings.coverstar.data.ContestData;

import java.util.ArrayList;

public class AlarmListAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private final ArrayList<AlarmItem> itemList = new ArrayList<>();

    public AlarmListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.alarm_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AlarmItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        // TODO 알람 데이터 세팅
    }

    public void setData(ArrayList<AlarmItem> dataList) {
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
            AlarmItem info = itemList.get(position);
            if (info == null) {
                return;
            }
        }
    }
}
