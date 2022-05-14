package com.shinleeholdings.coverstar.ui.custom;

import static com.shinleeholdings.coverstar.util.Util.getDisplayCountString;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.ui.fragment.ContestDetailFragment;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.Util;

public class ContestItemLayout extends CardView {

	private Context mContext;
	private MainActivity mMainActivity;

	ImageView contestImageView;

	TextView playCountTextView;
	TextView commentCountTextView;
	TextView likeCountTextView;
	TextView songTitleTextView;
	TextView singerNameTextView;
	TextView originalSingerNameTextView;

	private ContestData mContestItem;

	public ContestItemLayout(Context context) {
		this(context, null);
	}

	public ContestItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_contest_item, this, true);

		setCardBackgroundColor(context.getColor(R.color.subTextColor_6a6a6a));
		setPreventCornerOverlap(false);
		setUseCompatPadding(false);
		setCardElevation(0f);
		setRadius(Util.convertDpToPixel(7f, context));
		mContext = context;

		contestImageView = findViewById(R.id.contestImageView);
		playCountTextView = findViewById(R.id.playCountTextView);
		commentCountTextView = findViewById(R.id.commentCountTextView);
		likeCountTextView = findViewById(R.id.likeCountTextView);
		songTitleTextView = findViewById(R.id.songTitleTextView);
		singerNameTextView = findViewById(R.id.singerNameTextView);
		originalSingerNameTextView = findViewById(R.id.originalSingerNameTextView);
	}

	public void setData(MainActivity activity, ContestData data) {
		mMainActivity = activity;
		mContestItem = data;

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ContestDetailFragment contestDetailFragment = new ContestDetailFragment();
				Bundle bundle = new Bundle();
				bundle.putParcelable(AppConstants.EXTRA.CONTEST_DATA, mContestItem);
				contestDetailFragment.setArguments(bundle);

				mMainActivity.onFragmentInteractionCallback(contestDetailFragment);
			}
		});

		ImageLoader.loadImage(contestImageView, mContestItem.getBgImagePath());

		// TODO 아이콘 받아서 적용 필요
		playCountTextView.setText(getDisplayCountString(mContestItem.watchCnt));
		commentCountTextView.setText(getDisplayCountString(mContestItem.episode));
		// TODO 별표 개수 표시
		likeCountTextView.setText(getDisplayCountString(mContestItem.episode));

		songTitleTextView.setText(mContestItem.getTitle());
		singerNameTextView.setText(mContestItem.getNickName());
		originalSingerNameTextView = findViewById(R.id.originalSingerNameTextView);
	}
}
