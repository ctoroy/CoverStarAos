package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentPrevMediaBinding;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

public class PrevMediaFragment extends BaseFragment {

    private FragmentPrevMediaBinding binding;
    private PrevMediaListAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPrevMediaBinding.inflate(inflater, container, false);
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
        binding.prevMediaSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.prevMediaSwipeRefreshLayout.setOnRefreshListener(this::requestData);

        binding.searchImageView.setOnClickListener(view -> addFragment(new SearchFragment()));
        binding.alarmImageView.setOnClickListener(view -> addFragment(new AlarmListFragment()));

        binding.coverstarSeasonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 시즌 선택 팝업 제공 및 선택시 API 다시 호출
                binding.selectedSeasonTextView.setText("");
            }
        });

        mListAdapter = new PrevMediaListAdapter((MainActivity) getActivity());
        mListAdapter.setSortType(new SortFilterDialog.ISortTypeSelectListener() {
            @Override
            public void onSortTypeSelected(SortFilterDialog.SortType type) {
                requestData();
            }
        });
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

    private void requestData() {
        binding.prevMediaSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());

        // TODO 시즌 라운드 및 정렬 값으로 데이터 받아오기
//        mListAdapter.mSelectedSortType;


//        ArrayList<ContestData> epilogue = new ArrayList<>();
//        ArrayList<ContestData> itemList = new ArrayList<>();
//        for(int i = 0; i < 10; i++) {
//            ContestData item = new ContestData();
//            epilogue.add(item);
//
//            itemList.add(item);
//            itemList.add(item);
//            itemList.add(item);
//            itemList.add(item);
//        }
//        mListAdapter.setData(epilogue, itemList);

        ProgressDialogHelper.dismiss();
    }
}
