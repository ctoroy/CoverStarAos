package com.shinleeholdings.coverstar.ui.custom;

import static com.shinleeholdings.coverstar.util.Util.getDisplayCountString;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.util.ContestManager;

public class ContestItemLayout extends FrameLayout {

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
				ContestManager.getSingleInstance().showContestDetailFragment(mMainActivity, mContestItem);
			}
		});

		// TODO UI 업데이트 및 데이터 세팅

		// TODO 아이콘 업데이트 필요
		playCountTextView.setText(getDisplayCountString(200));
		commentCountTextView.setText(getDisplayCountString(200));
		likeCountTextView.setText(getDisplayCountString(200));
	}
}
