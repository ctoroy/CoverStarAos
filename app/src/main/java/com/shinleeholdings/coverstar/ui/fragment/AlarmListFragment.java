package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentAlarmlistBinding;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

public class AlarmListFragment extends BaseFragment {

    private FragmentAlarmlistBinding binding;

    private AlarmListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlarmlistBinding.inflate(inflater, container, false);
        initView();
        requestData();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.alarm));
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

        binding.alarmListSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.alarmListSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        binding.alarmListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AlarmListAdapter(getContext());
        binding.alarmListRecyclerView.setAdapter(mAdapter);
    }

    private void requestData() {
        // TODO 알람 리스트 요청
        ProgressDialogHelper.show(getActivity());
//        mAdapter.setData();
        binding.alarmListSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.dismiss();
    }
}
