package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentHomeBinding;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class HomeFragment extends BaseFragment implements ContestManager.IContestInfoUpdateListener {

    private FragmentHomeBinding binding;
    private SortFilterDialog.SortType selectedSortType = SortFilterDialog.SortType.LATEST;

    private HomePagerAdapter mHomePagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        setFilterInfo();
        ContestManager.getSingleInstance().addInfoChangeListener(this);
        requestData();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        ContestManager.getSingleInstance().removeInfoChangeListener(this);
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.homeSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.homeSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        binding.searchImageView.setOnClickListener(view -> addFragment(new SearchFragment()));
        binding.noticeImageView.setOnClickListener(view -> addFragment(new NoticeListFragment()));

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

        // TODO ViewPager2로 바꿔봐야하나 notifyDataSetchanged하면 다시그리네...
        mHomePagerAdapter = new HomePagerAdapter((MainActivity) getActivity(), position -> binding.homeViewPager.setCurrentItem(position, true));
        binding.homeViewPager.setAdapter(mHomePagerAdapter);
        binding.homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
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

    private boolean needNotifyDataSetChanged = false;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            if (needNotifyDataSetChanged) {
                needNotifyDataSetChanged = false;
                mHomePagerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onWatchCountUpdated(ContestData item) {
        if (mHomePagerAdapter.updateCount(item)) {
            needNotifyDataSetChanged = true;
        }
        DebugLogger.i("contestUpdate home onWatchCountUpdated needNotifyDataSetChanged : " + needNotifyDataSetChanged);
    }

    @Override
    public void onVoteCountUpdated(ContestData item) {
        if (mHomePagerAdapter.updateCount(item)) {
            needNotifyDataSetChanged = true;
        }
        DebugLogger.i("contestUpdate home onVoteCountUpdated needNotifyDataSetChanged : " + needNotifyDataSetChanged);
    }
}
