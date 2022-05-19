package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.data.ReplyItem;
import com.shinleeholdings.coverstar.databinding.FragmentContestDetailBinding;
import com.shinleeholdings.coverstar.ui.ContestPlayerActivity;
import com.shinleeholdings.coverstar.ui.custom.ContestItemLayout;
import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.model.DefaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class ContestDetailFragment extends BaseFragment {

    private FragmentContestDetailBinding binding;

    private CommentListAdapter mCommentListAdapter;
    private ReplyListAdapter mReplyListAdapter;

    private ContestData mContestItem;

    private String userImagePath;

    private ListenerRegistration commentChangeEventListener;
    private ListenerRegistration replyChangeEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContestDetailBinding.inflate(inflater, container, false);

        ContestData contestData = null;
        if (getArguments() != null) {
            contestData = getArguments().getParcelable(AppConstants.EXTRA.CONTEST_DATA);
        }
        binding.contestDetailSwipeRefreshLayout.setVisibility(View.GONE);

        if (contestData != null) {
            userImagePath = contestData.getUserImagePath();
            initView(contestData.castCode);
            requestContestDetail(contestData.castCode);
        }

        return binding.getRoot();
    }

    private void initView(String castCode) {
        binding.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.mediaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.slidingDrawer.isOpened()) {
                    binding.slidingDrawer.animateClose();
                    return;
                }
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

        binding.addToPlayListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialogHelper.show(getActivity());

                HashMap<String, String> param = new HashMap<>();
                param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
                param.put("castCode", mContestItem.castCode);

                // TODO 재생목록에 추가 API 처리
                RetroClient.getApiInterface().addToPlayList(param).enqueue(new RetroCallback<DefaultResult>() {
                    @Override
                    public void onSuccess(BaseResponse<DefaultResult> receivedData) {
                        ProgressDialogHelper.dismiss();
                        Toast.makeText(getActivity(), R.string.add_to_playlist_complete, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).myPageFragment.needRefreshPlayList = true;
                    }

                    @Override
                    public void onFailure(BaseResponse<DefaultResult> response) {
                        ProgressDialogHelper.dismiss();
                    }
                });
            }
        });

        binding.slidingDrawer.setOnDrawerCloseListener(() -> showCommentList());
        binding.closeReplyImageView.setOnClickListener(view -> showCommentList());

        binding.content.setOnClickListener(view -> {
            // nothing
        });

        binding.commentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentListAdapter = new CommentListAdapter((MainActivity) getActivity(), castCode, new CommentListAdapter.ICommentListEventListener() {
            @Override
            public void onWriteClicked() {
                showWriteComment();
            }

            @Override
            public void onCommentClicked(CommentItem item) {
                showReplyList(item);
            }
        });
        binding.commentListRecyclerView.setAdapter(mCommentListAdapter);

        binding.replyListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mReplyListAdapter = new ReplyListAdapter((MainActivity) getActivity(), castCode);
        binding.replyListRecyclerView.setAdapter(mReplyListAdapter);
    }

    private void showWriteComment() {
        // TODO 화면 하단 댓글 영역 및 키보드 노출
    }

    private void showWriteReply() {
        // TODO 화면 하단 답글 영역 및 키보드 노출
    }

    private void requestStarVote(int selectedStarCount) {
        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        param.put("voteCnt", selectedStarCount + "");
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        param.put("castCode", mContestItem.castCode);
        RetroClient.getApiInterface().setVote(param).enqueue(new RetroCallback<DefaultResult>() {
            @Override
            public void onSuccess(BaseResponse<DefaultResult> receivedData) {
                updateVote(selectedStarCount);
                mContestItem.addTotalLikeCount(selectedStarCount);
                binding.starCountTextView.setText(mContestItem.getTotalLikeCount() + "");
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onFailure(BaseResponse<DefaultResult> response) {
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

    private void setContestInfo() {
        binding.titleTextView.setText(mContestItem.getTitle());
        binding.songTitleTextview.setText(mContestItem.getTitle());

        ImageLoader.loadImage(binding.songImageView, mContestItem.getBgImagePath());

        binding.starCountTextView.setText(mContestItem.getTotalLikeCount() + "");

        ImageLoader.loadImage(binding.singerImageView, userImagePath);

        binding.singerNameTextview.setText(mContestItem.getNickName());

        binding.songEtcTextview.setText(String.format(getString(R.string.original_singer_format), mContestItem.logoImage));

        binding.songPlayCountTextview.setText(mContestItem.watchCnt + "");
        binding.dateTextview.setText(Util.changeFormattedDate(mContestItem.getUploadDate(), "yyyymmddhhmmss"));
        binding.contestDescriptionTextview.setText(mContestItem.sortBig);

        updateVote(mContestItem.episode);

        if (mContestItem.isFollow()) {
            binding.followTextView.setText(getString(R.string.unfollow));
        } else {
            binding.followTextView.setText(getString(R.string.follow));
        }
    }

    private void requestContestDetail(String castCode) {
        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        param.put("castCode", castCode);
        RetroClient.getApiInterface().getContestDetail(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ContestDataList result = receivedData.data;
                if (result.size() > 0) {
                    mContestItem = result.get(0);
                    setContestInfo();
                    requestAdditinalData();
                    initCommentData();
                } else {
                    ProgressDialogHelper.dismiss();
                }
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private void requestAdditinalData() {
        HashMap<String, String> param = new HashMap<>();
        param.put("castId", mContestItem.castId);
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        param.put("castStartDate", mContestItem.castStartDate);
        param.put("castCode", mContestItem.castCode);

        RetroClient.getApiInterface().getUserVodList(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();
                ContestDataList result = receivedData.data;
                binding.relatedMediaListLayout.removeAllViews();

                for (int i = 0; i < result.size(); i++) {
                    ContestItemLayout layout = new ContestItemLayout(getActivity());
                    layout.setData((MainActivity) getActivity(), result.get(i));
                    binding.relatedMediaListLayout.addView(layout);

                    if (i < result.size() - 1) {
                        View divider = new View(getActivity());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(1, Util.dpToPixel(getActivity(), 20f)));
                        binding.relatedMediaListLayout.addView(divider);
                    }
                }
                binding.contestDetailSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();
                binding.relatedMediaListLayout.removeAllViews();
                binding.contestDetailSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initCommentData() {
        String castCode = mContestItem.castCode;
        CommentHelper.getSingleInstance().getCommentList(castCode, new CommentHelper.ICommentEventListener() {
            @Override
            public void onCommentListLoaded(ArrayList<CommentItem> commentList) {
                DebugLogger.i("commentTest onCommentListLoaded : " + commentList.size());
                if (isFragmentRemoved()) {
                    return;
                }

                commentChangeEventListener = CommentHelper.getSingleInstance().getCommentListRef(mContestItem.castCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        try {
                            if (queryDocumentSnapshots != null) {
                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    String id = dc.getDocument().getId();
                                    Map<String, Object> data = dc.getDocument().getData();
                                    DebugLogger.i("commentTest commentChangeEvent type : " + dc.getType() + ", data : " + data);
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        CommentItem item = CommentHelper.getSingleInstance().getCommentItem(data);
                                        item.id = id;
                                        mCommentListAdapter.addComment(item);
                                        updateCommentCountText();
                                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                        mCommentListAdapter.removeComment(id);
                                        updateCommentCountText();
                                    } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                        CommentItem item = CommentHelper.getSingleInstance().getCommentItem(data);
                                        item.id = id;
                                        mCommentListAdapter.changeComment(item);
                                    }

                                    // TODO 답글 리스트 보여지고 있다면 해당 부분도 처리 필요
                                }
                            }
                        } catch (Exception exception) {
                        }
                    }
                });

                binding.slidingDrawer.setVisibility(View.VISIBLE);

                showCommentList();
                mCommentListAdapter.setData(commentList);
                updateCommentCountText();
            }
        });
    }

    private void updateCommentCountText() {
        binding.commentCountTextView.setText("(" + mCommentListAdapter.getItemCount() + ")");

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
        if (replyChangeEventListener != null) {
            replyChangeEventListener.remove();
        }
    }

    private void showReplyList(CommentItem item) {
        binding.commentListTitleLayout.setVisibility(View.INVISIBLE);
        binding.commentListRecyclerView.setVisibility(View.GONE);

        binding.replyListTitleLayout.setVisibility(View.VISIBLE);
        binding.replyListRecyclerView.setVisibility(View.VISIBLE);
        mReplyListAdapter.clear();

        ProgressDialogHelper.show(getActivity());
        CommentHelper.getSingleInstance().getReplyList(mContestItem.castCode, item, new CommentHelper.IReplyLoadListener() {
            @Override
            public void onReplyListLoaded(CommentItem comment, ArrayList<ReplyItem> replyList) {
                ProgressDialogHelper.dismiss();

                if (isFragmentRemoved() || binding.slidingDrawer.isOpened() == false || binding.replyListTitleLayout.getVisibility() != View.VISIBLE) {
                    return;
                }

                replyChangeEventListener = CommentHelper.getSingleInstance().getReplyListRef(mContestItem.castCode, comment.id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        try {
                            if (queryDocumentSnapshots != null) {
                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    String id = dc.getDocument().getId();
                                    Map<String, Object> data = dc.getDocument().getData();
                                    DebugLogger.i("commentTest replyChangeEvent type : " + dc.getType() + ", data : " + data);
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        ReplyItem item = CommentHelper.getSingleInstance().getReplyItem(id, data);
                                        mReplyListAdapter.addReply(item);
                                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                        mReplyListAdapter.removeReply(id);
                                    } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                        ReplyItem item = CommentHelper.getSingleInstance().getReplyItem(id, data);
                                        mReplyListAdapter.changeReply(item);
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            DebugLogger.exception(exception);
                        }
                    }
                });
                // TODO 코멘트 레이아웃, 답글리스트 UI 설정
            }
        });
    }

    private void writeComment(String comment) {
        if (NetworkHelper.isNetworkConnected() == false) {
            Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialogHelper.show(getActivity());
        CommentHelper.getSingleInstance().writeCommentItem(mContestItem, comment, new CommentHelper.IFireStoreActionCompleteListener() {
            @Override
            public void onCompleted() {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private void writeReply(CommentItem targetComment, String reply) {
        if (NetworkHelper.isNetworkConnected() == false) {
            Toast.makeText(MyApplication.getContext(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialogHelper.show(getActivity());
        CommentHelper.getSingleInstance().writeReplyItem(mContestItem, targetComment.id, reply, new CommentHelper.IFireStoreActionCompleteListener() {
            @Override
            public void onCompleted() {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private int getSelectedStarCount() {
        if (binding.voteStar5ImageView.isSelected()) {return 5;}
        if (binding.voteStar4ImageView.isSelected()) {return 4;}
        if (binding.voteStar3ImageView.isSelected()) {return 3;}
        if (binding.voteStar2ImageView.isSelected()) {return 2;}
        if (binding.voteStar1ImageView.isSelected()) {return 1;}
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

        if (myStarVote >= 1) {
            binding.voteStar1ImageView.setSelected(true);
        }
        if (myStarVote >= 2) {
            binding.voteStar2ImageView.setSelected(true);
        }
        if (myStarVote >= 3) {
            binding.voteStar3ImageView.setSelected(true);
        }
        if (myStarVote >= 4) {
            binding.voteStar4ImageView.setSelected(true);
        }
        if (myStarVote >= 5) {
            binding.voteStar5ImageView.setSelected(true);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO 댓글이나 답글 write 영역 노출되어있는 경우 사라지게 만들기

        if (binding.slidingDrawer.isOpened()) {
            binding.slidingDrawer.animateClose();
            return;
        }

        super.onBackPressed();
    }
}
