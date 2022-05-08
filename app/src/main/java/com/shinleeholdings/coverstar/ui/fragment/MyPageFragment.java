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
import com.shinleeholdings.coverstar.databinding.FragmentMypageBinding;
import com.shinleeholdings.coverstar.util.ImageLoader;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

public class MyPageFragment extends BaseFragment {

    private FragmentMypageBinding binding;

    private ContestListAdapter playListAdapter;
    private ContestListAdapter participateListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMypageBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {

        binding.myPageSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.myPageSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        ImageLoader.loadImage(binding.userImageView, LoginHelper.getSingleInstance().getLoginUserImagePath());
        binding.userNicknameTextView.setText(LoginHelper.getSingleInstance().getLoginUserNickName());

        binding.messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        binding.settingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new SettingFragment());
            }
        });

        binding.myCoinCountTextView.setText(Util.getFormattedCountString(LoginHelper.getSingleInstance().getMyCoinCount()));
        binding.buyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 구매
            }
        });
        binding.depositeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 입금
            }
        });
        binding.withdrawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 출금
            }
        });

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

                if (playListAdapter.getItemCount() <= 0) {
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

        // TODO 데이터 요청

//        ArrayList<ContestData> itemList = new ArrayList<>();
//        for(int i = 0; i < 10; i++) {
//            ContestData item = new ContestData();
//            itemList.add(item);
//            itemList.add(item);
//            itemList.add(item);
//            itemList.add(item);
//        }
//
//        if (binding.playListTextView.isSelected()) {
//            playListAdapter.setData(itemList);
//        } else {
//            participateListAdapter.setData(itemList);
//        }

        ProgressDialogHelper.dismiss();
    }
}
