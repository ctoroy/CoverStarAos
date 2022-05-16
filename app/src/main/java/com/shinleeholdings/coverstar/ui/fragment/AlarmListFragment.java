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
        binding.alarmListSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());
        // TODO 알람 리스트 디비에서 불러오기
//        mAdapter.setData();
        ProgressDialogHelper.dismiss();
    }

//    class SearchKeywordResult(
//            @SerializedName("LASTUPDT") var lastUpdateTime: String,
//            @SerializedName("SALE_STOCKS") var keywordDataList: ArrayList<SearchKeywordData>?)
//
//
//open class SearchKeywordData : RealmObject() {
//        @PrimaryKey
//        @SerializedName("PDNO")
//        open var scode: String = "" // 종목 코드
//
//        @SerializedName("STOCK_ID")
//        open var stockId: Int = 0
//
//        @SerializedName("KEYWORDS")
//        open var keywordList: RealmList<String> = RealmList()
//    }


//    val config = RealmConfiguration.Builder().allowWritesOnUiThread(true).allowQueriesOnUiThread(true).build()
//    val defaultRealm = Realm.getInstance(config)
//
//                    if (keywordResult.keywordDataList != null && keywordResult.keywordDataList!!.size > 0) {
//        defaultRealm.executeTransaction {
//            defaultRealm.insertOrUpdate(keywordResult.keywordDataList!!)
//        }
//    }
//
//    keywordList = defaultRealm.copyFromRealm(defaultRealm.where(SearchKeywordData::class.java).findAll()) as ArrayList<SearchKeywordData>?
}
