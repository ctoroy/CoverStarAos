package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentSearchBinding;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.NetworkHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestDataList;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class SearchFragment extends BaseFragment implements ContestManager.IContestInfoUpdateListener {

    private FragmentSearchBinding binding;
    private ContestListAdapter mAdapter;

    private final Handler searchHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        initView();
        ContestManager.getSingleInstance().addInfoChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        ContestManager.getSingleInstance().removeInfoChangeListener(this);
        super.onDestroyView();
        binding = null;
    }

    private void initView() {

        binding.titleLayout.titleTextView.setText(getString(R.string.search));
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                final String requestKeyword = charSequence.toString();
                searchRunnable = () -> requestSearch(requestKeyword);
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ContestListAdapter((MainActivity) getActivity());

        binding.searchResultRecyclerView.setAdapter(mAdapter);
    }

    private Runnable searchRunnable;

    private void requestSearch(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            binding.searchResultRecyclerView.setVisibility(View.GONE);
            binding.noSearchResultView.setVisibility(View.GONE);
            return;
        }

        if (NetworkHelper.isNetworkConnected() == false) {
            return;
        }

        HashMap<String, String> param = new HashMap<>();
        param.put("keyword", searchText);
        RetroClient.getApiInterface().getLiveListName(param).enqueue(new RetroCallback<ContestDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestDataList> receivedData) {
                ContestDataList itemList = receivedData.data;

                if (itemList.size() <= 0) {
                    binding.searchResultRecyclerView.setVisibility(View.GONE);
                    binding.noSearchResultView.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.setData(itemList);
                    binding.searchResultRecyclerView.setVisibility(View.VISIBLE);
                    binding.noSearchResultView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(BaseResponse<ContestDataList> response) {
                binding.searchResultRecyclerView.setVisibility(View.GONE);
                binding.noSearchResultView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onWatchCountUpdated(ContestData item) {
        mAdapter.updateCount(item);
    }

    @Override
    public void onVoteCountUpdated(ContestData item) {
        mAdapter.updateCount(item);
    }
}
