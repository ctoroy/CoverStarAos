package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentParticipateBinding;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

public class ParticipateFragment extends BaseFragment {

    private FragmentParticipateBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentParticipateBinding.inflate(inflater, container, false);
        initView();
        updateMyCoinCount();
        requestSeasonList();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            updateMyCoinCount();
        }
    }

    private void updateMyCoinCount() {
        binding.myCoinCountTextView.setText(Util.getCoinDisplayCountString(LoginHelper.getSingleInstance().getMyCoinCount()));
    }

    private void initView() {
        binding.coverstarSeasonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.selectedSeasonTextView.setText("");
                // TODO 시즌 선택 팝업 제공 및 선택시 API 다시 호출
            }
        });
        binding.paticipateCountTextView.setText(String.format(getString(R.string.participate_count), Util.getCoinDisplayCountString(AppConstants.PARTICIPATE_COIN_COUNT)));
        binding.registTextView.setOnClickListener(view -> startRegist());
    }

    private void startRegist() {
        String selectedSeasonRound = binding.selectedSeasonTextView.getText().toString();
        if (TextUtils.isEmpty(selectedSeasonRound)) {
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

        int myCoin = LoginHelper.getSingleInstance().getMyCoinCount();
        int participateCoin = AppConstants.PARTICIPATE_COIN_COUNT;
        if (myCoin < participateCoin) {
            DialogHelper.showPointCheckPopup(getActivity());
            return;
        }

        DialogHelper.showRegistConfirmPopup(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 참가 신청 API 호출하고 코인 차감? 화면 초기화 하기
            }
        });
    }

    private void requestSeasonList() {
        ProgressDialogHelper.show(getActivity());
        // TODO 참가신청 가능한 시즌 목록 가져와서 세팅
        binding.selectedSeasonTextView.setText("");

        ProgressDialogHelper.dismiss();
    }

}
