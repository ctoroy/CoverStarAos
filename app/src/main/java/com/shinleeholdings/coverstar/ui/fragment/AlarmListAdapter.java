package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.AlarmItem;
import com.shinleeholdings.coverstar.util.ImageLoader;

import java.util.ArrayList;

public class AlarmListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final ArrayList<AlarmItem> itemList = new ArrayList<>();
    int selectedItemIndex = -1;

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

        viewHolder.alarmTypeTextView.setText("Alarm");
        viewHolder.alarmTitleTextView.setText("알림타이틀");
        viewHolder.arrowImageView.setSelected(item.isSelected);
        if (item.isSelected) {
            viewHolder.contentsLayout.setVisibility(View.VISIBLE);
            String imagePath = "";
            if (TextUtils.isEmpty(imagePath)) {
                viewHolder.alarmImageView.setVisibility(View.GONE);
            } else {
                viewHolder.alarmImageView.setVisibility(View.VISIBLE);
                ImageLoader.loadImage(viewHolder.alarmImageView, "");
            }
            viewHolder.alarmContentsTextView.setText("알림컨텐츠");
        } else {
            viewHolder.contentsLayout.setVisibility(View.GONE);
        }
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

        TextView alarmTypeTextView;
        TextView alarmTitleTextView;
        ImageView arrowImageView;

        LinearLayout contentsLayout;
        ImageView alarmImageView;
        TextView alarmContentsTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            LinearLayout alarmTitleLayout = itemView.findViewById(R.id.alarmTitleLayout);
            alarmTitleLayout.setOnClickListener(this);

            alarmTypeTextView = itemView.findViewById(R.id.alarmTypeTextView);
            alarmTitleTextView = itemView.findViewById(R.id.alarmTitleTextView);
            arrowImageView = itemView.findViewById(R.id.arrowImageView);

            contentsLayout = itemView.findViewById(R.id.contentsLayout);
            alarmImageView = itemView.findViewById(R.id.alarmImageView);
            alarmContentsTextView = itemView.findViewById(R.id.alarmContentsTextView);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            AlarmItem item = itemList.get(position);
            if (item == null) {
                return;
            }

            if (item.isSelected) {
                item.isSelected = false;
                selectedItemIndex = -1;
            } else {
                if (selectedItemIndex != -1) {
                    AlarmItem selectedItem = itemList.get(selectedItemIndex);
                    selectedItem.isSelected = false;
                    notifyItemChanged(selectedItemIndex);
                }

                item.isSelected = true;
                selectedItemIndex = position;
            }
            notifyItemChanged(position);
        }
    }
}
