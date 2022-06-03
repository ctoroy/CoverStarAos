package com.shinleeholdings.coverstar.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.ui.dialog.CommentEditFilterDialog;
import com.shinleeholdings.coverstar.ui.fragment.CommentListAdapter;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

public class CommentItemLayout extends LinearLayout implements View.OnClickListener {

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
	private String mCastCode;
	CommentListAdapter.ICommentListEventListener mCommentEventListener;

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

	public void setData(MainActivity activity, String castCode, CommentItem item, CommentListAdapter.ICommentListEventListener commentEventListener) {
		mMainActivity = activity;
		mCommentItem = item;
		mCastCode = castCode;
		mCommentEventListener = commentEventListener;

		commentInfoLayout.setOnClickListener(this);
		listIconImageView.setOnClickListener(this);
		likeLayout.setOnClickListener(this);
		unLikeLayout.setOnClickListener(this);
		commentLayout.setOnClickListener(this);
		ImageLoader.loadImage(userImageView, item.userImagePath);
		userNicknameTextView.setText(item.userNickName);
		commentTimeTextView.setText(Util.changeFormattedDate(item.messageDate, AppConstants.COMMON_TIME_FORMAT));
		commentTextView.setText(item.message);

		likeCountTextView.setText(item.getLikeCount() + "");
		unLikeCountTextView.setText(item.getUnLikeCount() + "");
		commentCountTextView.setText(item.commentCount + "");

		likeLayout.setSelected(item.alreadyLike());
		unLikeLayout.setSelected(item.alreadyUnLike());
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.listIconImageView) {
			CommentEditFilterDialog dialog = new CommentEditFilterDialog(mMainActivity);
			dialog.init(mCommentItem, new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					int viewId = view.getId();
					if (NetworkHelper.isNetworkConnected() == false) {
						Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
						return;
					}

					if (viewId == R.id.deleteLayout) {
						ProgressDialogHelper.show(mMainActivity);
						CommentHelper.getSingleInstance().deleteCommentItem(mCastCode, mCommentItem, new CommentHelper.IFireStoreActionCompleteListener() {
							@Override
							public void onCompleted() {
								ProgressDialogHelper.dismiss();
								Toast.makeText(MyApplication.getContext(), R.string.delete_done, Toast.LENGTH_SHORT).show();
							}
						});
					} else if (viewId ==R.id.reportLayout) {
						if (mCommentItem.addReport()) {
							HashMap<String, Object> valueMap = new HashMap<>();
							valueMap.put(CommentHelper.FIELDNAME_REPORTS, mCommentItem.reports);
							ProgressDialogHelper.show(mMainActivity);
							CommentHelper.getSingleInstance().updateCommentItem(mCastCode, mCommentItem, valueMap, new CommentHelper.IFireStoreActionCompleteListener() {
								@Override
								public void onCompleted() {
									ProgressDialogHelper.dismiss();
									Toast.makeText(MyApplication.getContext(), R.string.report_done, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				}
			});
			dialog.show();
		} else if (viewId == R.id.recommendLayout){
			if (NetworkHelper.isNetworkConnected() == false) {
				Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
				return;
			}

			HashMap<String, Object> valueMap = new HashMap<>();
			if (mCommentItem.alreadyLike()) {
				// 좋아요 취소
				mCommentItem.removeLike();
				valueMap.put(CommentHelper.FIELDNAME_LIKES, mCommentItem.likes);
			} else {
				if (mCommentItem.alreadyUnLike()) {
					//  비추천 취소
					mCommentItem.removeUnLike();
					valueMap.put(CommentHelper.FIELDNAME_UNLIKES, mCommentItem.unLikes);
				}
				// 좋아요
				mCommentItem.addLike();
				valueMap.put(CommentHelper.FIELDNAME_LIKES, mCommentItem.likes);
			}

			ProgressDialogHelper.show(mMainActivity);
			CommentHelper.getSingleInstance().updateCommentItem(mCastCode, mCommentItem, valueMap, () -> ProgressDialogHelper.dismiss());
		} else if (viewId == R.id.unLikeLayout){
			if (NetworkHelper.isNetworkConnected() == false) {
				Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
				return;
			}

			HashMap<String, Object> valueMap = new HashMap<>();
			if (mCommentItem.alreadyUnLike()) {
				// 비추천 취소
				mCommentItem.removeUnLike();
				valueMap.put(CommentHelper.FIELDNAME_UNLIKES, mCommentItem.unLikes);
			} else {
				if (mCommentItem.alreadyLike()) {
					// 좋아요 취소
					mCommentItem.removeLike();
					valueMap.put(CommentHelper.FIELDNAME_LIKES, mCommentItem.likes);
				}
				// 비추천
				mCommentItem.addUnLike();
				valueMap.put(CommentHelper.FIELDNAME_UNLIKES, mCommentItem.unLikes);
			}

			ProgressDialogHelper.show(mMainActivity);
			CommentHelper.getSingleInstance().updateCommentItem(mCastCode, mCommentItem, valueMap, () -> ProgressDialogHelper.dismiss());
		} else if (viewId == R.id.commentLayout || viewId == R.id.commentInfoLayout){
			if (mCommentEventListener != null) {
				mCommentEventListener.onCommentClicked(mCommentItem);
			}
		}
	}
}
