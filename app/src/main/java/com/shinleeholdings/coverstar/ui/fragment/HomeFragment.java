package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentHomeBinding;
import com.shinleeholdings.coverstar.ui.dialog.SortFilterDialog;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private SortFilterDialog.SortType selectedSortType = SortFilterDialog.SortType.LATEST;

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
        ArrayList<ContestData> itemList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            ContestData item = new ContestData();
            itemList.add(item);
            itemList.add(item);
            itemList.add(item);
            itemList.add(item);
        }

        // TODO 이벤트 노티스 정리
//        public String castCode; //방키 이벤트:이벤트키
//        public String castId; //방장 아이디 이벤트:coverstar
//        public Integer watchCnt; //플레이수
//        public String castTitle; //타이틀 이벤트:이벤트타이틀
//        public Integer category; //경연종류 : 0:커버스타,1:다른경연(예:펫스타)
//        public Integer castPath;
//        public String nickName; //참가자명 이벤트:몇월라운드
//        public String profileImage; //썸네
//        public Integer castType; //0:커버스타이벤트,1:일반 이벤트,3:영상목록
//        public String castStartDate; //업로드일 이벤트:참여시작일
//        public String castEndDate; //이벤트:참여종료일
//        public Integer episode; //댓글수
//        public String logoImage; //원곡가수명 이벤트:총상금
//        public String sortBig; //한마디
//        public String sortMid;
//        public String sortSmall;
//        public String location; //영상 URL
//        public String store;
//        public String product; //경연가격 포인트
//        public String likes; //투표수 이벤트:입상자발표일
//        public String accumWatchCnt; //플레이수
        mHomeListAdapter.setData(null, itemList);

        ProgressDialogHelper.dismiss();
    }
}
