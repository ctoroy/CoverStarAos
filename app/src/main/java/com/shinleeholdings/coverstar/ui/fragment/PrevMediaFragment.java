package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentPrevMediaBinding;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.model.ContestGroupDataList;
import network.model.ContestInfoItem;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class PrevMediaFragment extends BaseFragment {
    private FragmentPrevMediaBinding binding;

    private ContestGroupDataList mContestInfoDataList;
    private ContestInfoItem selectedContestInfoItem;

    private PrevMediaListAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPrevMediaBinding.inflate(inflater, container, false);
        initView();
        requestSeasonList();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.prevMediaSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.prevMediaSwipeRefreshLayout.setOnRefreshListener(this::requestSeasonList);

        binding.searchImageView.setOnClickListener(view -> addFragment(new SearchFragment()));
        // TODO 이미지 변경 : 공지사항으로
        binding.noticeImageView.setOnClickListener(view -> addFragment(new NoticeListFragment()));

        mListAdapter = new PrevMediaListAdapter((MainActivity) getActivity());
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mListAdapter.getItemViewType(position)) {
                    case PrevMediaListAdapter.ITEM_TYPE_EPILOGUE:
                    case PrevMediaListAdapter.ITEM_TYPE_MEDIA_HEADER:
                        return 2;
                }

                return 1;
            }
        });
        binding.prevMediaRecyclerView.setItemViewCacheSize(200);
        binding.prevMediaRecyclerView.setLayoutManager(mLayoutManager);
        binding.prevMediaRecyclerView.setAdapter(mListAdapter);
    }

    private void requestSeasonList() {
        binding.prevMediaSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("temp", "1");
        RetroClient.getApiInterface().getContestList(param).enqueue(new RetroCallback<ContestGroupDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestGroupDataList> receivedData) {

                mContestInfoDataList = receivedData.data;
                initSpinner();

                if (mContestInfoDataList.size() > 0) {
                    selectedContestInfoItem = mContestInfoDataList.get(0);
                    requestData();
                } else {
                    ProgressDialogHelper.dismiss();
                }
            }

            @Override
            public void onFailure(BaseResponse<ContestGroupDataList> response) {
                ProgressDialogHelper.dismiss();
                setContestInfoItem(null);
            }
        });
    }

    private void setContestInfoItem(ContestInfoItem item) {
        selectedContestInfoItem = item;
        if (item != null) {
        }
    }

    private void initSpinner() {
        String[] items = new String[mContestInfoDataList.size()];
        for(int i=0; i <mContestInfoDataList.size(); i++) {
            ContestInfoItem item = mContestInfoDataList.get(i);
            items[i] = item.contestTitle;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.drop_down_list_item, R.id.selectedItemTextView, items);
        adapter.setDropDownViewResource(R.layout.drop_down_list_select_item);

        binding.contestInfoItemSpinner.setDropDownVerticalOffset(Util.convertDimenResIdToPixel(getActivity(), R.dimen.spinner_height));
        binding.contestInfoItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ContestInfoItem selectedItem = mContestInfoDataList.get(i);
                if (selectedContestInfoItem != null && selectedContestInfoItem.contestId == selectedItem.contestId) {
                    return;
                }
                selectedContestInfoItem = selectedItem;
                ProgressDialogHelper.show(getActivity());
                requestData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.contestInfoItemSpinner.setAdapter(adapter);
    }

    private void requestData() {
        HashMap<String, String> param = new HashMap<>();
        param.put("castStartDate", selectedContestInfoItem.contestStartDate);
        RetroClient.getApiInterface().getLastList(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ProgressDialogHelper.dismiss();
                ContestDataList itemList = receivedData.data;
                Util.sortList(mListAdapter.mSelectedSortType, itemList);
                mListAdapter.setData(null, itemList);
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                ProgressDialogHelper.dismiss();

            }
        });

        ProgressDialogHelper.dismiss();
    }
}
