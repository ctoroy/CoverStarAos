package com.shinleeholdings.coverstar.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.PaymentItem;
import com.shinleeholdings.coverstar.databinding.FragmentPointPaymentBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

import network.model.BaseResponse;
import network.model.CurCoinItem;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class PaymentItemListFragment extends BaseFragment {

    private FragmentPointPaymentBinding binding;

    private PaymentItemListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPointPaymentBinding.inflate(inflater, container, false);
        initView();
        setItemList();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.charge_point));
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

        binding.myPointCountTextView.setText(Util.getCoinDisplayCountString(LoginHelper.getSingleInstance().getMyCoinCount()));

        binding.paymentItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new PaymentItemListAdapter(getActivity(), new PaymentItemListAdapter.IPaymentItemClickEventListener() {
            @Override
            public void onItemClicked(PaymentItem item) {
                Intent intent = new Intent(getActivity(), PaymentWebViewActivity.class);
                intent.putExtra(AppConstants.EXTRA.WEBVIEW_URL, getRequestUrl(item));
                startActivityForResult(intent, AppConstants.REQUEST_CODE.PAYMENT);
            }
        });
        binding.paymentItemRecyclerView.setAdapter(mAdapter);
    }

    private String getRequestUrl(PaymentItem item) {
//        AMOUNT : 3000 (가격)
//        ORDERID : ORDER + NAME + YYYYMMDDHHMMSS (나중에 주문 확인용 맘대로 유니크)
//        ORDER : 3000P (제품명)
//        NAME : 8201031240677 (구매자명)

        selectedPaymentItem = item;
        String amount = item.payAmount + "";
        String order = String.format(MyApplication.getContext().getString(R.string.coin_count_format), (item.itemCount + ""));
        String name = LoginHelper.getSingleInstance().getLoginUserId();
        requestOrderId = order + name + Util.getCurrentTimeToFormat("yyyyMMddHHmmss");
        String payUrl = "";
        if (DebugLogger.IS_DEBUG) {
            payUrl = AppConstants.PAY_URL_DEV;
        } else {
            payUrl = AppConstants.PAY_URL_REAL;
        }

        return payUrl + "?p="+amount+"&o="+requestOrderId+"&n="+order+"&c="+name;
    }

    private PaymentItem selectedPaymentItem;
    private String requestOrderId;

    private void setItemList() {
        ArrayList<PaymentItem> itemList = new ArrayList<>();
//        3,000P/3,000원
//        9,000P/ 8,550원 (5% discount)
//        15,000P/13,500원 (10% discount)

        PaymentItem item3000 = new PaymentItem();
        item3000.itemCount = 3000;
        item3000.payAmount = 3000;
        item3000.discountPercent = "";
        itemList.add(item3000);

        PaymentItem item9000 = new PaymentItem();
        item9000.itemCount = 9000;
        item9000.payAmount = 8500;
        item9000.discountPercent = "5%";
        itemList.add(item9000);

        PaymentItem item15000 = new PaymentItem();
        item15000.itemCount = 15000;
        item15000.payAmount = 13500;
        item15000.discountPercent = "10%";
        itemList.add(item15000);

        mAdapter.setData(itemList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE.PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (selectedPaymentItem != null) {
                    requestAddCoin();
                }
            }
        }
    }

    private void requestAddCoin() {
        ProgressDialogHelper.show(getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        param.put("useCoinAmt", selectedPaymentItem.itemCount + "");
        param.put("mcId", requestOrderId);
        param.put("useType", "0"); // add

        RetroClient.getApiInterface().useCoin(param).enqueue(new RetroCallback<CurCoinItem>() {
            @Override
            public void onSuccess(BaseResponse<CurCoinItem> receivedData) {
                selectedPaymentItem = null;
                requestOrderId = "";

                ProgressDialogHelper.dismiss();
                CurCoinItem data = receivedData.data;
                int point = 0;
                if (TextUtils.isEmpty(data.currentCoin) == false) {
                    point = Integer.parseInt(data.currentCoin);
                }
                LoginHelper.getSingleInstance().setMyCoin(point);
                binding.myPointCountTextView.setText(Util.getCoinDisplayCountString(LoginHelper.getSingleInstance().getMyCoinCount()));
            }

            @Override
            public void onFailure(BaseResponse<CurCoinItem> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }
}
