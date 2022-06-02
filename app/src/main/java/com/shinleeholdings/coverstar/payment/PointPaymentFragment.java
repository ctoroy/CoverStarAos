package com.shinleeholdings.coverstar.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentPointPaymentBinding;
import com.shinleeholdings.coverstar.databinding.FragmentPurchaseBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestGroupDataList;
import network.model.CurCoinItem;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class PointPaymentFragment extends BaseFragment {

    private FragmentPointPaymentBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPointPaymentBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.deposite_point));
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void startPayment(String amount, String order) {
        Intent intent = new Intent(getActivity(), PaymentWebViewActivity.class);
        intent.putExtra(AppConstants.EXTRA.AMOUNT, amount);
        intent.putExtra(AppConstants.EXTRA.ORDER, order);
        startActivityForResult(intent, AppConstants.REQUEST_CODE.PAYMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE.PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                requestPointCheck();
            }
        }
    }

    private void requestPointCheck() {
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        RetroClient.getApiInterface().getCurCoin(param).enqueue(new RetroCallback<CurCoinItem>() {
            @Override
            public void onSuccess(BaseResponse<CurCoinItem> receivedData) {
                ProgressDialogHelper.dismiss();
                CurCoinItem data = receivedData.data;
                if (TextUtils.isEmpty(data.currentCoin) == false) {
                    LoginHelper.getSingleInstance().setMyCoin(Integer.parseInt(data.currentCoin));
                }
            }

            @Override
            public void onFailure(BaseResponse<CurCoinItem> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }
}
