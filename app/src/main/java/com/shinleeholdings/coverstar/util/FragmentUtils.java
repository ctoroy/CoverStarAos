package com.shinleeholdings.coverstar.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.ui.fragment.AlarmListFragment;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.ui.fragment.ContestDetailFragment;
import com.shinleeholdings.coverstar.ui.fragment.DepositeFragment;
import com.shinleeholdings.coverstar.ui.fragment.NoticeListFragment;
import com.shinleeholdings.coverstar.ui.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;

public class FragmentUtils {

    private static final String TAG_SEPARATOR = ":";

    public static void updateStackIndex(ArrayList<String> list, String tabId) {
        while (list.indexOf(tabId) != 0) {
            int i = list.indexOf(tabId);
            Collections.swap(list, i, i - 1);
        }
    }

    public static void updateStackToIndexFirst(ArrayList<String> stackList, String tabId) {
        int stackListSize = stackList.size();
        int moveUp = 1;
        while (stackList.indexOf(tabId) != stackListSize - 1) {
            int i = stackList.indexOf(tabId);
            Collections.swap(stackList, moveUp++, i);
        }
    }

    public static void updateTabStackIndex(ArrayList<String> tabList, String tabId) {
        if (!tabList.contains(tabId)) {
            tabList.add(tabId);
        }

        while (tabList.indexOf(tabId) != 0) {
            int i = tabList.indexOf(tabId);
            Collections.swap(tabList, i, i - 1);
        }
    }

    public static boolean isTabLayoutGoneFragment(BaseFragment fragment) {
        try {
            if (fragment instanceof AlarmListFragment) {
                return true;
            }
            if (fragment instanceof NoticeListFragment) {
                return true;
            }
            if (fragment instanceof SettingFragment) {
                return true;
            }
            if (fragment instanceof DepositeFragment) {
                return true;
            }
            if (fragment instanceof ContestDetailFragment) {
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

