package com.shinleeholdings.coverstar.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CountryData;
import com.shinleeholdings.coverstar.util.DialogHelper;

import java.util.ArrayList;

public class CountryListAdapter extends RecyclerView.Adapter {

    private ArrayList<CountryData> itemList = new ArrayList<>();
    DialogHelper.ICountrySelectListener listener;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.layout_country_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CountryData item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        // TODO 국가 이미지 설정
        viewHolder.countryNameTextView.setText(item.getName());
        viewHolder.countryCodeTextView.setText(item.getCode());
    }

    public void setItemList(ArrayList<CountryData> list, DialogHelper.ICountrySelectListener listener) {
        itemList.clear();
        itemList = list;

        this.listener = listener;

        notifyDataSetChanged();
    }

    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
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

        AppCompatImageView flagImageView;

        TextView countryNameTextView;
        TextView countryCodeTextView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            flagImageView = itemView.findViewById(R.id.countryFlagImageView);
            countryNameTextView = itemView.findViewById(R.id.countryNameTextView);
            countryCodeTextView = itemView.findViewById(R.id.countryCodeTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            CountryData country = itemList.get(position);
            listener.onCountrySelected(country);
        }
    }
}
