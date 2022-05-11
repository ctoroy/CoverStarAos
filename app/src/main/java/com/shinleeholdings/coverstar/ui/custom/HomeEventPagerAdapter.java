package com.shinleeholdings.coverstar.ui.custom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;

import java.util.ArrayList;

public class HomeEventPagerAdapter extends PagerAdapter {
    ArrayList<ContestData> itemList = new ArrayList<>();
    IPageMoveEventListener moveEventListener;

    public interface IPageMoveEventListener {
        void onMove(int position);
    }

    public void setData(ArrayList<ContestData> list, IPageMoveEventListener listener) {
        itemList.clear();
        moveEventListener = listener;

        if (list != null && list.size() > 0) {
            itemList.addAll(list);
        }
        notifyDataSetChanged();
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
        RelativeLayout itemView = (RelativeLayout) inflater.inflate(R.layout.layout_home_pager_item, container, false);

        ImageView homeNoticeBgImageView = itemView.findViewById(R.id.homeNoticeBgImageView);
        TextView homeLeftTitleSelectedTextView = itemView.findViewById(R.id.homeLeftTitleSelectedTextView);
        LinearLayout homeRightTitleUnSelectedLayout = itemView.findViewById(R.id.homeRightTitleUnSelectedLayout);
        TextView homeRightTitleSelectedTextView = itemView.findViewById(R.id.homeRightTitleSelectedTextView);
        LinearLayout homeLeftTitleUnSelectedLayout = itemView.findViewById(R.id.homeLeftTitleUnSelectedLayout);

        final ContestData item = itemList.get(position);
        if (position == 0) {
            homeNoticeBgImageView.setImageResource(R.drawable.visual_bg1);
            homeLeftTitleSelectedTextView.setVisibility(View.VISIBLE);
            homeRightTitleUnSelectedLayout.setVisibility(View.VISIBLE);
            homeRightTitleUnSelectedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveEventListener.onMove(1);
                }
            });
            homeRightTitleSelectedTextView.setVisibility(View.GONE);
            homeLeftTitleUnSelectedLayout.setVisibility(View.GONE);
        } else {
            homeNoticeBgImageView.setImageResource(R.drawable.visual_bg2);
            homeRightTitleSelectedTextView.setVisibility(View.VISIBLE);
            homeLeftTitleUnSelectedLayout.setVisibility(View.VISIBLE);
            homeLeftTitleUnSelectedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveEventListener.onMove(0);
                }
            });
            homeLeftTitleSelectedTextView.setVisibility(View.GONE);
            homeRightTitleUnSelectedLayout.setVisibility(View.GONE);
        }
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
