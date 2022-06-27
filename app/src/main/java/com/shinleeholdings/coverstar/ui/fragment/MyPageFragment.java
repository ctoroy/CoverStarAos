package com.shinleeholdings.coverstar.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentMypageBinding;
import com.shinleeholdings.coverstar.payment.PaymentItemListFragment;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;
import retrofit2.Call;

public class MyPageFragment extends BaseFragment implements LoginHelper.ILoginUserInfoChangeEventListener, ContestManager.IContestInfoUpdateListener {

    private FragmentMypageBinding binding;

    private ContestListAdapter playListAdapter;
    private ContestListAdapter participateListAdapter;

    public boolean needRefreshPlayList = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMypageBinding.inflate(inflater, container, false);
        initView();
        ContestManager.getSingleInstance().addInfoChangeListener(this);
        LoginHelper.getSingleInstance().addUserInfoChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        ContestManager.getSingleInstance().removeInfoChangeListener(this);
        LoginHelper.getSingleInstance().removeUserInfoChangeListener(this);
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            if (binding.playListTextView.isSelected()) {
                if (needRefreshPlayList) {
                    needRefreshPlayList = false;
                    requestData();
                }
            }
        }
    }

    private void initView() {

        binding.myPageSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.myPageSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        ImageLoader.loadUserImage(binding.userImageView, LoginHelper.getSingleInstance().getLoginUserImagePath());
        binding.userNicknameTextView.setText(LoginHelper.getSingleInstance().getLoginUserNickName());

        binding.settingImageView.setOnClickListener(view -> addFragment(new SettingFragment()));

//        binding.messageImageView.setVisibility(View.VISIBLE);
//        binding.messageImageView.setOnClickListener(view -> addFragment(new ChattingListFragment()));

        binding.depositePointLayout.setOnClickListener(view -> addFragment(new PaymentItemListFragment()));

        onMyCoinUpdated(LoginHelper.getSingleInstance().getMyCoinCount());

        binding.buyLayout.setOnClickListener(view -> addFragment(new PurchaseFragment()));
        binding.depositeLayout.setOnClickListener(view -> addFragment(new DepositeFragment()));
        binding.withdrawLayout.setOnClickListener(view -> addFragment(new WithDrawFragment()));

        binding.playListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.playListTextView.isSelected()) {
                    return;
                }
                binding.playListTextView.setSelected(true);
                binding.playListTextView.setTypeface(binding.playListTextView.getTypeface(), Typeface.BOLD);
                Util.setTextViewBold(binding.playListTextView, true);
                binding.playListLine.setVisibility(View.VISIBLE);
                binding.playlistRecyclerView.setVisibility(View.VISIBLE);

                binding.participateListTextView.setSelected(false);
                binding.participateListTextView.setTypeface(binding.playListTextView.getTypeface(), Typeface.NORMAL);
                Util.setTextViewBold(binding.participateListTextView, false);
                binding.participateLine.setVisibility(View.INVISIBLE);
                binding.participateRecyclerView.setVisibility(View.GONE);

                if (playListAdapter.getItemCount() <= 0 || needRefreshPlayList) {
                    needRefreshPlayList = false;
                    requestData();
                }
            }
        });

        binding.participateListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.participateListTextView.isSelected()) {
                    return;
                }
                binding.participateListTextView.setSelected(true);
                binding.participateListTextView.setTypeface(binding.participateListTextView.getTypeface(), Typeface.BOLD);
                Util.setTextViewBold(binding.participateListTextView, true);
                binding.participateLine.setVisibility(View.VISIBLE);
                binding.participateRecyclerView.setVisibility(View.VISIBLE);

                binding.playListTextView.setSelected(false);
                binding.playListTextView.setTypeface(binding.playListTextView.getTypeface(), Typeface.NORMAL);
                Util.setTextViewBold(binding.playListTextView, false);
                binding.playListLine.setVisibility(View.INVISIBLE);
                binding.playlistRecyclerView.setVisibility(View.GONE);

                if (participateListAdapter.getItemCount() <= 0) {
                    requestData();
                }
            }
        });

        binding.playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playListAdapter = new ContestListAdapter((MainActivity) getActivity());
        binding.playlistRecyclerView.setAdapter(playListAdapter);

        binding.participateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        participateListAdapter = new ContestListAdapter((MainActivity) getActivity());
        binding.participateRecyclerView.setAdapter(participateListAdapter);

        binding.playListTextView.performClick();
    }

    private void requestData() {
        binding.myPageSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());

        HashMap<String, String> param = new HashMap<>();
        Call<BaseResponse<ContestDataList>> call = null;
        if (binding.playListTextView.isSelected()) {
            param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
            call = RetroClient.getApiInterface().getPlayList(param);
        } else {
            param.put("castId", LoginHelper.getSingleInstance().getLoginUserId());
            call = RetroClient.getApiInterface().getMyList(param);
        }

        call.enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();

                ContestDataList result = receivedData.data;
                if (binding.playListTextView.isSelected()) {
                    playListAdapter.setData(result);
                } else {
                    participateListAdapter.setData(result);
                }
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    @Override
    public void onMyCoinUpdated(int currentCoinCount) {
        binding.myCoinCountTextView.setText(Util.getCoinDisplayCountString(LoginHelper.getSingleInstance().getMyCoinCount()));
    }

    @Override
    public void onUserInfoUpdated() {
        ImageLoader.loadUserImage(binding.userImageView, LoginHelper.getSingleInstance().getLoginUserImagePath());
        binding.userNicknameTextView.setText(LoginHelper.getSingleInstance().getLoginUserNickName());
    }

    @Override
    public void onWatchCountUpdated(ContestData item) {
        playListAdapter.updateCount(item);
        participateListAdapter.updateCount(item);
    }

    @Override
    public void onVoteCountUpdated(ContestData item) {
        playListAdapter.updateCount(item);
        participateListAdapter.updateCount(item);
    }
}
