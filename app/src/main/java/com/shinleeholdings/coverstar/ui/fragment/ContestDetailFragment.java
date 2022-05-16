package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentContestDetailBinding;
import com.shinleeholdings.coverstar.ui.ContestPlayerActivity;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.model.defaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class ContestDetailFragment extends BaseFragment {

    private FragmentContestDetailBinding binding;

    private CommentListAdapter mCommentListAdapter;
    private ReplyListAdapter mReplyListAdapter;

    private ContestData mContestItem;


    private ListenerRegistration commentChangeEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContestDetailBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            mContestItem = getArguments().getParcelable(AppConstants.EXTRA.CONTEST_DATA);
        }
        binding.contestDetailSwipeRefreshLayout.setVisibility(View.GONE);

        if (mContestItem != null) {
            initView();
            requestAdditinalData();
            initCommentData();
        }

        return binding.getRoot();
    }

    private void initView() {
        binding.titleBackLayout.setOnClickListener(view -> finish());

        binding.reportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 정리 필요 : 신고하기
            }
        });

        binding.mediaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContestPlayerActivity.class);
                intent.putExtra(AppConstants.EXTRA.CONTEST_URL, mContestItem.location);
                startActivity(intent);
            }
        });

        binding.voteStar1ImageView.setOnClickListener(view -> setMyStarVote(1));
        binding.voteStar2ImageView.setOnClickListener(view -> setMyStarVote(2));
        binding.voteStar3ImageView.setOnClickListener(view -> setMyStarVote(3));
        binding.voteStar4ImageView.setOnClickListener(view -> setMyStarVote(4));
        binding.voteStar5ImageView.setOnClickListener(view -> setMyStarVote(5));

        binding.voteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedStarCount = getSelectedStarCount();
                if (selectedStarCount == 0) {
                    return;
                }
                requestStarVote(selectedStarCount);
            }
        });

        binding.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_TEXT, mContestItem.location);
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, getString(R.string.share)));
            }
        });

        binding.slidingDrawer.setOnDrawerCloseListener(() -> showCommentList());
        binding.closeReplyImageView.setOnClickListener(view -> showCommentList());

        binding.content.setOnClickListener(view -> {
            // nothing
        });

        binding.commentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentListAdapter = new CommentListAdapter((MainActivity) getActivity());
        binding.commentListRecyclerView.setAdapter(mCommentListAdapter);

        binding.replyListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mReplyListAdapter = new ReplyListAdapter((MainActivity) getActivity());
        binding.replyListRecyclerView.setAdapter(mReplyListAdapter);

        binding.titleTextView.setText(mContestItem.getTitle());
        binding.songTitleTextview.setText(mContestItem.getTitle());

        ImageLoader.loadImage(binding.songImageView, mContestItem.getBgImagePath());

        binding.starCountTextView.setText(mContestItem.getTotalLikeCount() + "");

        ImageLoader.loadImage(binding.singerImageView, mContestItem.getUserImagePath());

        binding.singerNameTextview.setText(mContestItem.getNickName());

        // TODO 부가정보 표시
        binding.songEtcTextview.setText("");

        binding.songPlayCountTextview.setText(mContestItem.watchCnt + "");
        binding.dateTextview.setText(Util.changeFormattedDate(mContestItem.getUploadDate(), "yyyymmddhhmmss"));
        binding.contestDescriptionTextview.setText(mContestItem.sortBig);
    }

    private void requestStarVote(int selectedStarCount) {
        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        param.put("voteCnt", selectedStarCount + "");
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        param.put("castCode", mContestItem.castCode);
        RetroClient.getApiInterface().setVote(param).enqueue(new RetroCallback<defaultResult>() {
            @Override
            public void onSuccess(BaseResponse<defaultResult> receivedData) {
                updateVote(selectedStarCount);
                mContestItem.addTotalLikeCount(selectedStarCount);
                binding.starCountTextView.setText(mContestItem.getTotalLikeCount() + "");
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onFailure(BaseResponse<defaultResult> response) {
                ProgressDialogHelper.dismiss();
            }
        });

    }

    private void updateVote(int votedStarCount) {
        setMyStarVote(votedStarCount);
        if (votedStarCount > 0) {
            binding.voteTextView.setText(getString(R.string.vote_complete));
            binding.voteTextView.setEnabled(false);
        } else {
            binding.voteTextView.setText(getString(R.string.vote));
            binding.voteTextView.setEnabled(true);
        }
    }

    private void requestAdditinalData() {
        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        param.put("castId", mContestItem.castId);
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        param.put("castStartDate", mContestItem.castStartDate);
        param.put("castCode", mContestItem.castCode);

        RetroClient.getApiInterface().getUserVodList(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();

                // TODO episode // 내가 투표한 별표 개수 받기
                int votedStarCount = 0;
                updateVote(votedStarCount);

                // TODO 개인별 지난영상 추가하기
                binding.relatedMediaListLayout.removeAllViews();
                ArrayList<ContestData> itemList = new ArrayList<>();

                // TODO 팔로우 처리, 클릭이벤트에서도 잘 처리 필요
                boolean isAlreadyFollow = false;
                if (isAlreadyFollow) {
                    binding.followTextView.setText(getString(R.string.unfollow));
                } else {
                    binding.followTextView.setText(getString(R.string.follow));
                }

                for (int i = 0; i < itemList.size(); i++) {
                    ContestItemLayout layout = new ContestItemLayout(getActivity());
                    layout.setData((MainActivity) getActivity(), itemList.get(i));
                    binding.relatedMediaListLayout.addView(layout);

                    if (i < itemList.size() - 1) {
                        View divider = new View(getActivity());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPixel(getActivity(), 20f), 1));
                        binding.relatedMediaListLayout.addView(divider);
                    }
                }
                binding.contestDetailSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();
                binding.contestDetailSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initCommentData() {
        // TODO test
        String castCode = "ctoroy20210726162622";
//        String castCode = mContestItem.castCode;

        CommentHelper.getSingleInstance().getCommentList(castCode, new CommentHelper.ICommentEventListener() {
            @Override
            public void onCommentListLoaded(ArrayList<CommentItem> commentList) {
                if (isFragmentRemoved()) {
                    return;
                }

                commentChangeEventListener = CommentHelper.getSingleInstance().getCommentListRef(mContestItem.castCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // TODO 코멘트 업데이트 처리(개수 변경, 아이템 데이터 변경)
                    }
                });

                binding.slidingDrawer.setVisibility(View.VISIBLE);

                binding.commentCountTextView.setText("(" + commentList.size()  +")");

                showCommentList();
                mCommentListAdapter.setData(commentList);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (commentChangeEventListener != null) {
            commentChangeEventListener.remove();
        }
        binding = null;
    }


    private void showCommentList() {
        binding.commentListTitleLayout.setVisibility(View.VISIBLE);
        binding.commentListRecyclerView.setVisibility(View.VISIBLE);

        binding.replyListTitleLayout.setVisibility(View.INVISIBLE);
        binding.replyListRecyclerView.setVisibility(View.GONE);
    }

    private void showReplyList() {
        binding.commentListTitleLayout.setVisibility(View.INVISIBLE);
        binding.commentListRecyclerView.setVisibility(View.GONE);

        binding.replyListTitleLayout.setVisibility(View.VISIBLE);
        binding.replyListRecyclerView.setVisibility(View.VISIBLE);
    }

    private int getSelectedStarCount() {
        if (binding.voteStar5ImageView.isSelected()) { return 5; }
        if (binding.voteStar4ImageView.isSelected()) { return 4; }
        if (binding.voteStar3ImageView.isSelected()) { return 3; }
        if (binding.voteStar2ImageView.isSelected()) { return 2; }
        if (binding.voteStar1ImageView.isSelected()) { return 1; }

        return 0;
    }

    private void setMyStarVote(int myStarVote) {
        binding.voteStar1ImageView.setSelected(false);
        binding.voteStar2ImageView.setSelected(false);
        binding.voteStar3ImageView.setSelected(false);
        binding.voteStar4ImageView.setSelected(false);
        binding.voteStar5ImageView.setSelected(false);

        if (myStarVote == 0) {
            return;
        }

        if (myStarVote >= 1) { binding.voteStar1ImageView.setSelected(true); }
        if (myStarVote >= 2) { binding.voteStar2ImageView.setSelected(true); }
        if (myStarVote >= 3) { binding.voteStar3ImageView.setSelected(true); }
        if (myStarVote >= 4) {  binding.voteStar4ImageView.setSelected(true); }
        if (myStarVote >= 5) { binding.voteStar5ImageView.setSelected(true); }
    }

    @Override
    public void onBackPressed() {
        if (binding.slidingDrawer.isOpened()) {
            binding.slidingDrawer.animateClose();
            return;
        }

        super.onBackPressed();
    }
}
