package com.shinleeholdings.coverstar.payment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.PaymentItem;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

public class PaymentItemListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final ArrayList<PaymentItem> itemList = new ArrayList<>();

    public interface IPaymentItemClickEventListener {
        void onItemClicked(PaymentItem item);
    }

    private IPaymentItemClickEventListener mItemClickListener;

    public PaymentItemListAdapter(Context context, IPaymentItemClickEventListener clickEventListener) {
        mContext = context;
        mItemClickListener = clickEventListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.list_item_point_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PaymentItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        viewHolder.nameTextView.setText(Util.getCoinDisplayCountString(item.itemCount));
        viewHolder.priceTextView.setText("(" + String.format(mContext.getString(R.string.price_display_format), Util.numberToDisplayFormat(item.payAmount)) + ")");
        if (TextUtils.isEmpty(item.discountPercent) == false) {
            viewHolder.discountInfoTextView.setVisibility(View.VISIBLE);
            viewHolder.discountInfoTextView.setText(String.format(mContext.getString(R.string.discount_display_format), item.discountPercent));
        } else {
            viewHolder.discountInfoTextView.setVisibility(View.GONE);
        }
    }

    public void setData(ArrayList<PaymentItem> dataList) {
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

        TextView nameTextView;
        TextView priceTextView;
        TextView discountInfoTextView;
        TextView exchangeTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.itemNameTextView);
            priceTextView = itemView.findViewById(R.id.itemPriceTextView);
            discountInfoTextView = itemView.findViewById(R.id.discountPercentTextView);
            exchangeTextView = itemView.findViewById(R.id.exchangeTextView);
            exchangeTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            PaymentItem item = itemList.get(position);
            if (item == null) {
                return;
            }

            if (mItemClickListener != null) {
                mItemClickListener.onItemClicked(item);
            }
        }
    }
}
