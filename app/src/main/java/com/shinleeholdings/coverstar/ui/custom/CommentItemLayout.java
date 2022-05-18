package com.shinleeholdings.coverstar.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.Util;

public class CommentItemLayout extends LinearLayout {

	private Context mContext;
	private MainActivity mMainActivity;

	RelativeLayout commentInfoLayout;
	ImageView userImageView;
	TextView userNicknameTextView;
	TextView commentTimeTextView;
	TextView commentTextView;
	ImageView listIconImageView;

	LinearLayout likeLayout;
	TextView likeCountTextView;

	LinearLayout unLikeLayout;
	TextView unLikeCountTextView;

	LinearLayout commentLayout;
	TextView commentCountTextView;

	private CommentItem mCommentItem;

	public CommentItemLayout(Context context) {
		this(context, null);
	}

	public CommentItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_comment_item, this, true);

		commentInfoLayout = findViewById(R.id.commentInfoLayout);

		userImageView = findViewById(R.id.userImageView);
		userNicknameTextView = findViewById(R.id.userNicknameTextView);
		commentTimeTextView = findViewById(R.id.commentTimeTextView);
		commentTextView = findViewById(R.id.commentTextView);
		listIconImageView = findViewById(R.id.listIconImageView);
		likeLayout = findViewById(R.id.recommendLayout);
		likeCountTextView = findViewById(R.id.recommendCountTextView);
		unLikeLayout = findViewById(R.id.unLikeLayout);
		unLikeCountTextView = findViewById(R.id.unLikeCountTextView);
		commentLayout = findViewById(R.id.commentLayout);
		commentCountTextView = findViewById(R.id.commentCountTextView);
	}

	public void setData(MainActivity activity, CommentItem item, View.OnClickListener clickListener) {
		mMainActivity = activity;
		mCommentItem = item;

		commentInfoLayout.setOnClickListener(clickListener);
		listIconImageView.setOnClickListener(clickListener);
		likeLayout.setOnClickListener(clickListener);
		unLikeLayout.setOnClickListener(clickListener);
		commentLayout.setOnClickListener(clickListener);


		ImageLoader.loadImage(userImageView, item.userImagePath);
		userNicknameTextView.setText(item.userNickName);
		commentTimeTextView.setText(Util.changeFormattedDate(item.messageDate, CommentHelper.COMMENT_TIME_FORMAT));
		commentTextView.setText(item.message);

		likeCountTextView.setText(item.getLikeCount() + "");
		unLikeCountTextView.setText(item.getUnLikeCount() + "");
		commentCountTextView.setText(item.getCommentCount() + "");

		likeLayout.setSelected(item.alreadyLike());
		unLikeLayout.setSelected(item.alreadyUnLike());
	}
}
