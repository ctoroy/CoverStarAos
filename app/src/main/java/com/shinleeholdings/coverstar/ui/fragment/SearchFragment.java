package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.FragmentSearchBinding;
import com.shinleeholdings.coverstar.util.ContestManager;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding binding;
    private ContestListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {

        binding.titleLayout.titleTextView.setText(getString(R.string.search));
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    requestSearch();
                }
                return false;
            }
        });

        binding.searchImageView.setOnClickListener(view -> requestSearch());

        binding.searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ContestListAdapter(getContext(), new ContestManager.IContestItemClickListener() {
            @Override
            public void onContestClicked(ContestData contestData) {
                // TODO 콘테스트 클릭 이벤트 처리하기
            }
        });

        binding.searchResultRecyclerView.setAdapter(mAdapter);
    }

    private void requestSearch() {
        ProgressDialogHelper.show(getActivity());
        String inputText = binding.searchEditText.getText().toString();
        if (TextUtils.isEmpty(inputText)) {
            binding.searchResultRecyclerView.setVisibility(View.GONE);
            binding.noSearchResultView.setVisibility(View.GONE);
            return;
        }
        // TODO 검색 호출하기, 결과 유무로 세팅
//        ProgressDialogHelper.dismiss();
//        mAdapter.setData();
//        binding.searchResultRecyclerView.setVisibility(View.VISIBLE);
//        binding.noSearchResultView.setVisibility(View.VISIBLE);

    }
}
