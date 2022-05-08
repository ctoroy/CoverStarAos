package com.shinleeholdings.coverstar.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shinleeholdings.coverstar.FragmentInteractionCallback;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.OnFragmentBackPressed;
import com.shinleeholdings.coverstar.util.FragmentUtils;

public abstract class BaseFragment extends Fragment implements OnFragmentBackPressed {

    private FragmentInteractionCallback fragmentInteractionCallback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTabLayoutVisibility();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInteractionCallback = (FragmentInteractionCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionCallback = null;
    }

    public void finish() {
        getActivity().onBackPressed();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            updateTabLayoutVisibility();
        }
    }

    private void updateTabLayoutVisibility() {
        ((MainActivity)getActivity()).setTabLayoutVisibility(FragmentUtils.isTabLayoutGoneFragment(this) == false);
    }

    public void addFragment(BaseFragment targetFragment) {
        if (fragmentInteractionCallback != null) {
            Bundle bundle = new Bundle();
            fragmentInteractionCallback.onFragmentInteractionCallback(targetFragment);
        }
    }

    public boolean isFragmentRemoved() {
        return isDetached() || isRemoving() || isAdded() == false;
    }

    public BaseFragment getCurrentFragment() {
        return ((MainActivity)getActivity()).getCurrentFragment();
    }

    public void popBackStackImmediate() {
        ((MainActivity)getActivity()).popFragment();
    }

    @Override
    public void onBackPressed() {
        popBackStackImmediate();
    }
}
