package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.chatting.ChattingListFragment;
import com.shinleeholdings.coverstar.databinding.FragmentHomeBinding;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private SortFilterDialog.SortType selectedSortType = SortFilterDialog.SortType.LATEST;

    private HomePager2Adapter mHomePagerAdapter;

    public interface IPageMoveEventListener {
        void onPageMove(int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        setFilterInfo();
        requestData();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.homeSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.homeSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        binding.searchImageView.setOnClickListener(view -> addFragment(new SearchFragment()));
        binding.noticeImageView.setOnClickListener(view -> addFragment(new NoticeListFragment()));
        binding.messageImageView.setOnClickListener(view -> addFragment(new ChattingListFragment()));

        binding.homeFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortFilterDialog dialog = new SortFilterDialog(getActivity());
                dialog.init(selectedSortType, new SortFilterDialog.ISortTypeSelectListener() {
                    @Override
                    public void onSortTypeSelected(SortFilterDialog.SortType type) {
                        if (selectedSortType == type) {
                            return;
                        }

                        selectedSortType = type;
                        setFilterInfo();
                        mHomePagerAdapter.updateSort(selectedSortType);
                        mHomePagerAdapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
            }
        });

        mHomePagerAdapter = new HomePager2Adapter((MainActivity) getActivity(), position -> binding.homeViewPager.setCurrentItem(position, true));
        binding.homeViewPager.setAdapter(mHomePagerAdapter);
        binding.homeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                binding.homeSwipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void setFilterInfo() {
        switch (selectedSortType) {
            case POPULAR:
                binding.selectedFilterTextView.setText(getString(R.string.order_popular));
                break;
            case SEARCH:
                binding.selectedFilterTextView.setText(getString(R.string.order_search));
                break;
            case LATEST:
                binding.selectedFilterTextView.setText(getString(R.string.order_recently));
                break;
        }
    }

    private void requestData() {
        binding.homeSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("temp", "1");

        RetroClient.getApiInterface().getHomeList(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();

                ContestDataList result = receivedData.data;
                mHomePagerAdapter.setData(result);
                mHomePagerAdapter.updateSort(selectedSortType);
                mHomePagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }
}
