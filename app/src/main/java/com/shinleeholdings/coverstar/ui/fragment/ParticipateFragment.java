package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentParticipateBinding;
import com.shinleeholdings.coverstar.profile.LaunchActivity;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.Date;
import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestGroupDataList;
import network.model.ContestInfoItem;
import network.model.CoverStarUser;
import network.model.DefaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class ParticipateFragment extends BaseFragment implements LoginHelper.ILoginUserInfoChangeEventListener {
    private ContestGroupDataList mContestInfoDataList;
    private ContestInfoItem selectedContestInfoItem;

    private FragmentParticipateBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentParticipateBinding.inflate(inflater, container, false);
        initView();
        updateMyCoinCount();

        int contestInfoId = -1;
        if (getArguments() != null) {
            contestInfoId = getArguments().getInt(AppConstants.EXTRA.CONTEST_INFO_ID, -1);
        }
        requestSeasonList(contestInfoId);
        LoginHelper.getSingleInstance().addUserInfoChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        LoginHelper.getSingleInstance().removeUserInfoChangeListener(this);
        super.onDestroyView();
        binding = null;
    }

    private void updateMyCoinCount() {
        binding.myCoinCountTextView.setText(Util.getCoinDisplayCountString(LoginHelper.getSingleInstance().getMyCoinCount()));
    }

    private void initView() {
        binding.participateSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.participateSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initInputInfo();
                requestSeasonList(-1);
            }
        });
        binding.registTextView.setOnClickListener(view -> startRegist());
        binding.paticipateCountTextView.setText("");
    }

    private void setContestInfoItem(ContestInfoItem item) {
        selectedContestInfoItem = item;
        if (item != null) {
            binding.paticipateCountTextView.setText(String.format(getString(R.string.participate_count), Util.getCoinDisplayCountString(item.contestPayAmt)));
        } else {
            binding.paticipateCountTextView.setText("");
        }
    }

    public void setContestInfoSelect(int contestId) {
        ContestInfoItem selectedItem = null;
        if (mContestInfoDataList != null && mContestInfoDataList.size() > 0) {
            for (int i = 0; i< mContestInfoDataList.size(); i++) {
                ContestInfoItem item = mContestInfoDataList.get(i);
                if (item.contestId == contestId) {
                    selectedItem = item;
                    break;
                }
            }

            if (selectedItem == null) {
                selectedItem = mContestInfoDataList.get(0);
            }
        }

        setContestInfoItem(selectedItem);
    }

    private void startRegist() {
        if (selectedContestInfoItem == null) {
            Toast.makeText(getActivity(), getString(R.string.select_season_round), Toast.LENGTH_SHORT).show();
            return;
        }

        String songTitle = binding.songTitleEditText.getText().toString();
        if (TextUtils.isEmpty(songTitle)) {
            Toast.makeText(getActivity(), getString(R.string.input_song_title), Toast.LENGTH_SHORT).show();
            return;
        }
        String singerName = binding.singerNameEditText.getText().toString();
        if (TextUtils.isEmpty(singerName)) {
            Toast.makeText(getActivity(), getString(R.string.input_singer_name), Toast.LENGTH_SHORT).show();
            return;
        }
        String mediaLink = binding.mediaLinkEditText.getText().toString();
        if (TextUtils.isEmpty(mediaLink)) {
            Toast.makeText(getActivity(), getString(R.string.input_media_link), Toast.LENGTH_SHORT).show();
            return;
        }
        String mention = binding.mentionEditText.getText().toString();
        if (TextUtils.isEmpty(mention)) {
            Toast.makeText(getActivity(), getString(R.string.input_mention), Toast.LENGTH_SHORT).show();
            return;
        }

        if (LoginHelper.getSingleInstance().getMyCoinCount() < selectedContestInfoItem.contestPayAmt) {
            DialogHelper.showPointCheckPopup(getActivity());
            return;
        }

        DialogHelper.showRegistConfirmPopup(getActivity(), selectedContestInfoItem.contestPayAmt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoverStarUser userData = LoginHelper.getSingleInstance().getSavedLoginUserData();
                if (userData == null) {
                    Intent intent = new Intent(getActivity(), LaunchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }

                HashMap<String, String> param = new HashMap<>();
                param.put("castId", userData.userId + "");
                param.put("castCode", userData.userId + Util.dateToFormatString(new Date(), AppConstants.COMMON_TIME_FORMAT));
                param.put("nickName", userData.nickName);
                param.put("profileImage", userData.userProfileImage);

                param.put("castTitle", songTitle);
                param.put("logoImage", singerName);
                param.put("location", mediaLink);
                param.put("sortBig", mention);

                param.put("category", "2");
                param.put("product", "");
                param.put("likes", "");
                param.put("episode", "");

                param.put("castType", selectedContestInfoItem.contestType + "");
                param.put("castPath", selectedContestInfoItem.contestId + "");
                param.put("castStartDate", selectedContestInfoItem.contestStartDate);
                param.put("castEndDate", selectedContestInfoItem.contestEndDate);
                param.put("store", selectedContestInfoItem.contestShotDate);
                param.put("sortSmall", selectedContestInfoItem.contestTitle);
                param.put("sortMid", selectedContestInfoItem.contestAward + "");

                ProgressDialogHelper.show(getActivity());
                RetroClient.getApiInterface().startBroadCast(param).enqueue(new RetroCallback<DefaultResult>() {
                    @Override
                    public void onSuccess(BaseResponse<DefaultResult> receivedData) {
                        ProgressDialogHelper.dismiss();
                        Toast.makeText(getActivity(), String.format(getString(R.string.regist_complete), selectedContestInfoItem.contestTitle), Toast.LENGTH_LONG).show();
                        LoginHelper.getSingleInstance().updateMyCoin(-selectedContestInfoItem.contestPayAmt);
                        initInputInfo();
                    }

                    @Override
                    public void onFailure(BaseResponse<DefaultResult> response) {
                        ProgressDialogHelper.dismiss();
                    }
                });
            }
        });
    }

    private void initInputInfo() {
        binding.songTitleEditText.setText("");
        binding.singerNameEditText.setText("");
        binding.mediaLinkEditText.setText("");
        binding.mentionEditText.setText("");
    }

    private void requestSeasonList(int contestId) {
        binding.participateSwipeRefreshLayout.setRefreshing(false);
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("temp", "1");
        RetroClient.getApiInterface().getContestList(param).enqueue(new RetroCallback<ContestGroupDataList>() {
            @Override
            public void onSuccess(BaseResponse<ContestGroupDataList> receivedData) {
                ProgressDialogHelper.dismiss();

                mContestInfoDataList = receivedData.data;
                initSpinner();
                if (contestId != -1) {
                    setContestInfoSelect(contestId);
                } else {
                    ContestInfoItem item = null;
                    if (mContestInfoDataList.size() > 0) {
                        item = mContestInfoDataList.get(0);
                    }
                    setContestInfoItem(item);
                }
            }

            @Override
            public void onFailure(BaseResponse<ContestGroupDataList> response) {
                ProgressDialogHelper.dismiss();
                setContestInfoItem(null);
            }
        });
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
                setContestInfoItem(mContestInfoDataList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.contestInfoItemSpinner.setAdapter(adapter);
    }

    @Override
    public void onMyCoinUpdated(int currentCoinCount) {
        updateMyCoinCount();
    }

    @Override
    public void onUserInfoUpdated() {
    }
}
