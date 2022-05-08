package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentHomeBinding;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private SortFilterDialog.SortType selectedSortType = SortFilterDialog.SortType.POPULAR;

    private HomeListAdapter mHomeListAdapter;

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
        binding.alarmImageView.setOnClickListener(view -> addFragment(new AlarmListFragment()));

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
                        requestData();
                    }
                });
            }
        });

        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHomeListAdapter = new HomeListAdapter((MainActivity) getActivity());
        binding.homeRecyclerView.setAdapter(mHomeListAdapter);
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

        // TODO
//        ArrayList<ContestData> itemList = new ArrayList<>();
//        for(int i = 0; i < 10; i++) {
//            ContestData item = new ContestData();
//            itemList.add(item);
//            itemList.add(item);
//            itemList.add(item);
//            itemList.add(item);
//        }
//        mHomeListAdapter.setData(null, itemList);

        ProgressDialogHelper.dismiss();
    }
}
