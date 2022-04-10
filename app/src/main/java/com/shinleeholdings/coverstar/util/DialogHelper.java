package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CountryData;
import com.shinleeholdings.coverstar.ui.CountryListAdapter;

import java.util.ArrayList;

public class DialogHelper {
    private static final boolean DIALOG_CANCELABLE = false;

    public static void showToast(Context context, String message, boolean center) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        if (center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();
    }

    public static void showCertNumSendCompletePopup(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_TransparentBackground);
        ViewGroup view = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.popup_phone_cert_send_complete, null);
        dialog.setContentView(view);

        FrameLayout closeLayout = view.findViewById(R.id.closeLayout);
        closeLayout.setOnClickListener(view1 -> dialog.dismiss());

        TextView countryListView = view.findViewById(R.id.certNumSendCompleteTextView);

        countryListView.setText(Util.getSectionOfTextColor(activity, R.color.color_f45786, activity.getString(R.string.cert_num_send_complete), activity.getString(R.string.send)));
        Button okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }


    public interface ICountrySelectListener {
        public void onCountrySelected(CountryData country);
    }

    public static void showCountrySelectPopup(Activity activity, ICountrySelectListener listener) {
        // TODO 국가 설정
        ArrayList<CountryData> countryDataArrayList = new ArrayList<>();
        countryDataArrayList.add(new CountryData("한국1", "+82", ""));
        countryDataArrayList.add(new CountryData("한국2", "+82", ""));
        countryDataArrayList.add(new CountryData("한국3", "+82", ""));
        countryDataArrayList.add(new CountryData("한국4", "+82", ""));
        countryDataArrayList.add(new CountryData("한국5", "+82", ""));
        countryDataArrayList.add(new CountryData("한국6", "+82", ""));
        countryDataArrayList.add(new CountryData("한국7", "+82", ""));
        countryDataArrayList.add(new CountryData("한국8", "+82", ""));
        countryDataArrayList.add(new CountryData("한국9", "+82", ""));
        countryDataArrayList.add(new CountryData("한국10", "+82", ""));
        countryDataArrayList.add(new CountryData("한국11", "+82", ""));
        countryDataArrayList.add(new CountryData("한국12", "+82", ""));
        countryDataArrayList.add(new CountryData("한국13", "+82", ""));
        countryDataArrayList.add(new CountryData("한국14", "+82", ""));
        countryDataArrayList.add(new CountryData("한국15", "+82", ""));
        countryDataArrayList.add(new CountryData("한국16", "+82", ""));
        countryDataArrayList.add(new CountryData("한국17", "+82", ""));
        countryDataArrayList.add(new CountryData("한국18", "+82", ""));

        final Dialog dialog = new Dialog(activity, R.style.Theme_TransparentBackground);
        ViewGroup view = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.layout_country_select, null);
        dialog.setContentView(view);

        FrameLayout closeLayout = view.findViewById(R.id.closeLayout);
        closeLayout.setOnClickListener(view1 -> dialog.dismiss());


        CountryListAdapter adapter = new CountryListAdapter();
        adapter.setItemList(countryDataArrayList, country -> {
            listener.onCountrySelected(country);
            dialog.dismiss();
        });

        RecyclerView countryListView = view.findViewById(R.id.countryRecyclerView);
        countryListView.setLayoutManager(new LinearLayoutManager(activity));
        countryListView.setAdapter(adapter);

        dialog.show();
    }
}
