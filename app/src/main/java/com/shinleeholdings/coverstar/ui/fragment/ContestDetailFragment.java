package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentContestDetailBinding;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;
import com.shinleeholdings.coverstar.ui.custom.MySlidingDrawer;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;

public class ContestDetailFragment extends BaseFragment {

    private FragmentContestDetailBinding binding;

    private CommentListAdapter mCommentListAdapter;
    private ReplyListAdapter mReplyListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContestDetailBinding.inflate(inflater, container, false);
        initView();

        if (getArguments() != null) {
            // TODO 콘테스트 아이디 가져오기
        }

        binding.contestDetailSwipeRefreshLayout.setVisibility(View.GONE);
        requestData();
        return binding.getRoot();
    }

    private void requestData() {
        ProgressDialogHelper.show(getActivity());

        // TODO 데이터 받아오기

        ProgressDialogHelper.dismiss();

        // TODO 노래 이름 입력
        binding.titleTextView.setText("");
        binding.songTitleTextview.setText("");

        // TODO 노래 이미지 세팅
        ImageLoader.loadImage(binding.songImageView, "");

        // TODO 별 개수 표시
        binding.starCountTextView.setText("123");

        // TODO 내가 투표한 경우 별 표시해주기
        setMyStarVote(0);
        boolean alreadyVote = false;
        if (alreadyVote) {
            binding.voteTextView.setText(getString(R.string.revote));
        } else {
            binding.voteTextView.setText(getString(R.string.vote));
        }

        // TODO 싱어 이미지 세팅
        ImageLoader.loadImage(binding.singerImageView, "");

        // TODO 싱어 이름 설정
        binding.singerNameTextview.setText("");

        // TODO 팔로우 처리, 클릭이벤트에서도 잘 처리 필요
        boolean isAlreadyFollow = false;
        if (isAlreadyFollow) {
            binding.followTextView.setText(getString(R.string.unfollow));
        } else {
            binding.followTextView.setText(getString(R.string.follow));
        }

        // TODO 부가정보 표시
        binding.songEtcTextview.setText("");
        binding.songPlayCountTextview.setText("");
        binding.dateTextview.setText("");
        binding.contestDescriptionTextview.setText("");

        // TODO 관련 영상 추가하기
        binding.relatedMediaListLayout.removeAllViews();
        ArrayList<ContestData> itemList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            ContestData item = new ContestData();
            itemList.add(item);
            itemList.add(item);
            itemList.add(item);
            itemList.add(item);
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

        ArrayList<CommentItem> commentItemList = new ArrayList<>();
        for(int i = 0; i < 70; i++) {
            CommentItem item = new CommentItem();
            commentItemList.add(item);
        }
        // TODO 코멘트 카운트추가
        binding.commentCountTextView.setText("(" + commentItemList.size()  +")");

        showCommentList();
        mCommentListAdapter.setData(commentItemList);

        binding.contestDetailSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleBackLayout.setOnClickListener(view -> finish());

        binding.reportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 신고하기
            }
        });

        binding.mediaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 비디오 재생 화면 시작
            }
        });

        binding.voteStar1ImageView.setOnClickListener(view -> setMyStarVote(1));
        binding.voteStar2ImageView.setOnClickListener(view -> setMyStarVote(2));
        binding.voteStar3ImageView.setOnClickListener(view -> setMyStarVote(3));
        binding.voteStar4ImageView.setOnClickListener(view -> setMyStarVote(4));
        binding.voteStar5ImageView.setOnClickListener(view -> setMyStarVote(1));

        binding.voteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 투표하기
                // TODO 투표하고 새로고침 한번 하자
            }
        });

        binding.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 공유하기 , 뭘 공유? 링크를???
            }
        });

        binding.addToPlayListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 재생목록에 추가
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
