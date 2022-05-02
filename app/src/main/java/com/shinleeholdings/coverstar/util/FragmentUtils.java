package com.shinleeholdings.coverstar.util;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shinleeholdings.coverstar.FragmentInteractionCallback;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.ui.fragment.AlarmListFragment;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;

import java.util.Map;
import java.util.Stack;

public class FragmentUtils {

    private static final String TAG_SEPARATOR = ":";

    public static final String TABNAME = ".TABNAME";
    public static final String SHOULD_ADD = ".SHOULD_ADD";

    public static boolean isTabLayoutGoneFragment(BaseFragment fragment) {
        try {
            if (fragment instanceof AlarmListFragment) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    private static void setAnimation(FragmentTransaction fragmentTransition) {
        fragmentTransition.setCustomAnimations(
                R.anim.fragment_fade_in,
                R.anim.fragment_fade_out
        );
    }

    public static String getFragmentKey(Fragment fragment) {
        return fragment.getClass().getSimpleName() + TAG_SEPARATOR + fragment.hashCode();
    }

    public static void addInitialTabFragment(FragmentManager fragmentManager,
                                             Map<String, Stack<String>> tagStacks,
                                             String tag,
                                             Fragment fragment,
                                             int layoutId,
                                             boolean shouldAddToStack) {
        String fragmentKey = getFragmentKey(fragment);
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        setAnimation(fragmentTransition);

        fragmentTransition.add(layoutId, fragment, fragmentKey).commitAllowingStateLoss();

        if (shouldAddToStack) {
            tagStacks.get(tag).push(fragmentKey);
        }
    }

    public static void addAdditionalTabFragment(FragmentManager fragmentManager,
                                             Map<String, Stack<String>> tagStacks,
                                             String tag, Fragment show, Fragment hideFragment,
                                             int layoutId, boolean shouldAddToStack) {
        String fragmentKey = getFragmentKey(show);
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        setAnimation(fragmentTransition);

        fragmentTransition.add(layoutId, show, fragmentKey).show(show);

        if (hideFragment != null) {
            fragmentTransition.hide(hideFragment);
        }

        fragmentTransition.commitAllowingStateLoss();

        if (shouldAddToStack) {
            tagStacks.get(tag).push(fragmentKey);
        }
    }

    public static void addShowHideFragment(FragmentManager fragmentManager,
                                           Map<String, Stack<String>> tagStacks,
                                           String tag, Fragment show, Fragment hideFragment,
                                           int layoutId, boolean shouldAddToStack) {
        String fragmentKey = getFragmentKey(show);
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        setAnimation(fragmentTransition);

        fragmentTransition.add(layoutId, show, fragmentKey).show(show);

        if (hideFragment != null) {
            fragmentTransition.hide(hideFragment);
        }

        fragmentTransition.commitAllowingStateLoss();

        if (shouldAddToStack) {
            tagStacks.get(tag).push(fragmentKey);
        }
    }

    public static void showHideTabFragment(FragmentManager fragmentManager, Fragment show, Fragment hideFragment) {
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        setAnimation(fragmentTransition);

        if (hideFragment != null) {
            fragmentTransition.hide(hideFragment);
        }
        fragmentTransition.show(show).commitAllowingStateLoss();
    }

    public static void removeFragment(FragmentManager fragmentManager, Fragment show, Fragment removeFragment) {
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        setAnimation(fragmentTransition);

        if (removeFragment != null) {
            fragmentTransition.remove(removeFragment);
        }
        fragmentTransition.show(show).commitAllowingStateLoss();
    }

    public static void removeTargetFragment(FragmentManager fragmentManager, Fragment removeFragment) {
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        fragmentTransition.remove(removeFragment).commitAllowingStateLoss();
    }

    public static void startFragment(
            BaseFragment targetFragment,
            String stackTabName,
            boolean shouldAdd,
            FragmentInteractionCallback fragmentInteractionCallback) {
        if (fragmentInteractionCallback != null) {
            Bundle bundle = new Bundle();
            bundle.putString(TABNAME, stackTabName);
            bundle.putBoolean(SHOULD_ADD, shouldAdd);
            fragmentInteractionCallback.onFragmentInteractionCallback(targetFragment, bundle);
        }
    }

//        fun getFragmentInstance(fragmentName: String): MainBaseFragment? = when (fragmentName) {
//            StockInfoFragment::class.simpleName -> StockInfoFragment()
//            TagStockListFragment::class.simpleName -> TagStockListFragment()
//            ThemeDetailFragment::class.simpleName -> ThemeDetailFragment()
//            StockOrderHistoryFragment::class.simpleName -> StockOrderHistoryFragment()
//            HasStockListFragment::class.simpleName -> HasStockListFragment()
//            PflsListFragment::class.simpleName -> PflsListFragment()
//            BankTransListFragment::class.simpleName -> BankTransListFragment()
//            TimeLineListFragment::class.simpleName -> TimeLineListFragment()
//            NoticeListFragment::class.simpleName -> NoticeListFragment()
//            NoticeDetailFragment::class.simpleName -> NoticeDetailFragment()
//            FaqListFragment::class.simpleName -> FaqListFragment()
//            AuthAndSecurityFragment::class.simpleName -> AuthAndSecurityFragment()
//            AuthManageFragment::class.simpleName -> AuthManageFragment()
//            OtpManageFragment::class.simpleName -> OtpManageFragment()
//            SettingFragment::class.simpleName -> SettingFragment()
//            DpsHistoryFragment::class.simpleName -> DpsHistoryFragment()
//            RankingContentsDetailFragment::class.simpleName -> RankingContentsDetailFragment()
//            AutoInvestDetailFragment::class.simpleName -> AutoInvestDetailFragment()
//            AutoInvestHistoryFragment::class.simpleName -> AutoInvestHistoryFragment()
//            CustomWebViewFragment::class.simpleName -> CustomWebViewFragment()
//            NoticeDetailFragment::class.java.simpleName -> NoticeDetailFragment()
//            BasketFragment::class.java.simpleName -> BasketFragment()
//            else -> null
//        }
}

