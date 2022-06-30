package com.shinleeholdings.coverstar;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.shinleeholdings.coverstar.chatting.ChatRoomListHelper;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.databinding.ActivityMainBinding;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.ui.fragment.CoverStarListFragment;
import com.shinleeholdings.coverstar.ui.fragment.HomeFragment;
import com.shinleeholdings.coverstar.ui.fragment.MyPageFragment;
import com.shinleeholdings.coverstar.ui.fragment.ParticipateFragment;
import com.shinleeholdings.coverstar.ui.fragment.PrevMediaFragment;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.FragmentUtils;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import network.model.BaseResponse;
import network.model.CurCoinItem;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class MainActivity extends BaseActivity implements FragmentInteractionCallback {

    private ActivityMainBinding binding;

    private static final int TAB_INDEX_HOME = 0;
    private static final int TAB_INDEX_PREV_MEDIA = 1;
    private static final int TAB_INDEX_COVERSTAR = 2;
    private static final int TAB_INDEX_PARTICIPATE = 3;
    private static final int TAB_INDEX_MYPAGE = 4;

    private enum TabMenuType {
        HOME(R.drawable.tab_menu_home_bg, R.string.tab_name_home),
        PREV_MEDIA(R.drawable.tab_menu_prev_media_bg, R.string.tab_name_prev_media),
        COVERSTAR(R.drawable.icon_center, 0),
        PARTICIPATE(R.drawable.tab_menu_participate_bg, R.string.tab_name_participate),
        MY_PAGE(R.drawable.tab_menu_mypage_bg, R.string.tab_name_mypage);

        public final int iconResId;
        public final int textResId;

        TabMenuType(int iconResId, int textResId) {
            this.iconResId = iconResId;
            this.textResId = textResId;
        }
    }

    private BaseFragment currentFragment;
    public static String mCurrentTab = "";

    protected LinkedHashMap<String, Stack<String>> tagStacks = new LinkedHashMap<>();
    protected ArrayList<String> stackList = new ArrayList<>();
    protected ArrayList<String> menuStacks = new ArrayList<>();

    private final HomeFragment homeFragment = new HomeFragment();
    private final PrevMediaFragment prevMediaFragment = new PrevMediaFragment();
    private final CoverStarListFragment coverStarFragment = new CoverStarListFragment();
    private final ParticipateFragment participateFragment = new ParticipateFragment();
    public final MyPageFragment myPageFragment = new MyPageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFragmentTabStackInfo();
        initUi();

        saveWidth();

        mCurrentTab = "";

        selectedTab(TabMenuType.HOME.toString());

        new Handler().postDelayed(() -> requestPointCheck(), 500);
    }

    private void requestPointCheck() {
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", LoginHelper.getSingleInstance().getLoginUserId());
        RetroClient.getApiInterface().getCurCoin(param).enqueue(new RetroCallback<CurCoinItem>() {
            @Override
            public void onSuccess(BaseResponse<CurCoinItem> receivedData) {
                CurCoinItem data = receivedData.data;
                int point = 0;
                if (TextUtils.isEmpty(data.currentCoin) == false) {
                    point = Integer.parseInt(data.currentCoin);
                }
                LoginHelper.getSingleInstance().setMyCoin(point);
            }

            @Override
            public void onFailure(BaseResponse<CurCoinItem> response) {
            }
        });
    }

    private void saveWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.DISPLAY_WIDTH, width);
    }

    private void initFragmentTabStackInfo() {
        tagStacks.put(TabMenuType.HOME.toString(), new Stack<>());
        tagStacks.put(TabMenuType.PREV_MEDIA.toString(), new Stack<>());
        tagStacks.put(TabMenuType.COVERSTAR.toString(), new Stack<>());
        tagStacks.put(TabMenuType.PARTICIPATE.toString(), new Stack<>());
        tagStacks.put(TabMenuType.MY_PAGE.toString(), new Stack<>());

        stackList.add(TabMenuType.HOME.toString());
        stackList.add(TabMenuType.PREV_MEDIA.toString());
        stackList.add(TabMenuType.COVERSTAR.toString());
        stackList.add(TabMenuType.PARTICIPATE.toString());
        stackList.add(TabMenuType.MY_PAGE.toString());

        menuStacks.add(TabMenuType.HOME.toString());
    }

    private void initUi() {
        for (int i=0; i <TabMenuType.values().length; i++) {
            LinearLayout tabMenuLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tab_menu_item, null);

            TabMenuType tabInfo = TabMenuType.values()[i];

            AppCompatImageView iconImageView = tabMenuLayout.findViewById(R.id.tab_menu_image);
            iconImageView.setImageResource(tabInfo.iconResId);

            TextView tabTitleTextView = tabMenuLayout.findViewById(R.id.tab_menu_title);
            if (tabInfo.textResId == 0) {
                tabTitleTextView.setVisibility(View.GONE);
            } else {
                tabTitleTextView.setVisibility(View.VISIBLE);
                tabTitleTextView.setText(getString(tabInfo.textResId));
            }

            tabMenuLayout.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            tabMenuLayout.setPadding(0,0,0,0);

            TabLayout.Tab currentTab = binding.tabLayout.newTab();
            currentTab.setCustomView(tabMenuLayout);
            currentTab.setTag(i);
            binding.tabLayout.addTab(currentTab);
        }

        binding.tabLayout.setTabRippleColorResource(R.color.colorAccent);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTabId = getTabKey((int)tab.getTag());
                selectedTab(selectedTabId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                popStackExceptFirst();
            }
        });
    }

    private void popStackExceptFirst() {
        Stack<String> currentTagStacks = tagStacks.get(mCurrentTab);
        if (currentTagStacks.size() == 1) {
            // Nothing , 나중에 스크롤 탑 하던지..
        } else {
            clearTabStack(currentTagStacks);
        }
    }

    private void clearTabStack(Stack<String> tagStack) {
        while (tagStack.size() > 1) {
            Fragment peekFragment = getSupportFragmentManager().findFragmentByTag(tagStack.peek());
            if (peekFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(peekFragment);
            }
            tagStack.pop();
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tagStack.elementAt(0));
        if (fragment != null) {
            FragmentUtils.removeFragment(getSupportFragmentManager(), fragment, getCurrentFragment());
            assignCurrentFragment((BaseFragment) fragment);

        }
    }

    private void selectedTab(String tabId) {
        if (mCurrentTab.equals(tabId)) {
            return;
        }

        mCurrentTab = tabId;
        if (tagStacks.get(tabId).size() == 0) {
            if (tabId.equals(TabMenuType.HOME.toString())) {
                initTab(tabId, homeFragment, true);
            } else if (tabId.equals(TabMenuType.PREV_MEDIA.toString())) {
                initTab(tabId, prevMediaFragment, false);
            } else if (tabId.equals(TabMenuType.PARTICIPATE.toString())) {
                initTab(tabId, participateFragment, false);
            } else if (tabId.equals(TabMenuType.MY_PAGE.toString())) {
                initTab(tabId, myPageFragment, false);
            } else if (tabId.equals(TabMenuType.COVERSTAR.toString())) {
                initTab(tabId, coverStarFragment, false);
            }
        } else {
            BaseFragment targetFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tagStacks.get(tabId).lastElement());

            FragmentUtils.showHideTabFragment(getSupportFragmentManager(), targetFragment, getCurrentFragment());
            resolveStackLists(tabId);
            assignCurrentFragment(targetFragment);
        }
    }

    private void resolveStackLists(String tabId) {
        FragmentUtils.updateStackIndex(stackList, tabId);
        FragmentUtils.updateTabStackIndex(menuStacks, tabId);
    }

    private void initTab(String tabId, BaseFragment fragment, Boolean isInitialFragment) {
        if (isInitialFragment) {
            clearFragmentsFromContainer();
            FragmentUtils.addInitialTabFragment(getSupportFragmentManager(), tagStacks, tabId, fragment, R.id.mainContainer, true);
        } else {
            FragmentUtils.addAdditionalTabFragment(getSupportFragmentManager(), tagStacks, tabId, fragment, getCurrentFragment(), R.id.mainContainer, true);
        }

        resolveStackLists(tabId);
        assignCurrentFragment(fragment);
    }

    private void clearFragmentsFromContainer() {
        try {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments.size() > 0) {
                FragmentTransaction fragmentTransition = getSupportFragmentManager().beginTransaction();
                for (int i=0; i < fragments.size(); i++) {
                    Fragment fragment = fragments.get(i);
                    fragmentTransition.detach(fragment);
                    fragmentTransition.remove(fragment);
                }
                fragmentTransition.commitAllowingStateLoss();
            }
            getSupportFragmentManager().executePendingTransactions();
        } catch (Exception e){
        }
    }

    private String getTabKey(int tabIndex) {
        switch (tabIndex) {
            case TAB_INDEX_HOME:
                return TabMenuType.HOME.toString();
            case TAB_INDEX_PREV_MEDIA:
                return TabMenuType.PREV_MEDIA.toString();
            case TAB_INDEX_PARTICIPATE:
                return TabMenuType.PARTICIPATE.toString();
            case TAB_INDEX_MYPAGE:
                return TabMenuType.MY_PAGE.toString();
            case TAB_INDEX_COVERSTAR:
                return TabMenuType.COVERSTAR.toString();
            default:
                return "";
        }
    }

    public void registContest(ContestData contestData) {
        if (tagStacks.get(TabMenuType.PARTICIPATE.toString()).size() == 0) {
            // 탭 한번도 오픈안함
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.EXTRA.CONTEST_INFO_ID, contestData.castPath);
            participateFragment.setArguments(bundle);
            binding.tabLayout.getTabAt(MainActivity.TAB_INDEX_PARTICIPATE).select();
        } else {
            // 탭 오픈했음
            binding.tabLayout.getTabAt(MainActivity.TAB_INDEX_PARTICIPATE).select();
            participateFragment.setContestInfoSelect(contestData.castPath);
        }
    }

    private void setCurrentTabSelected() {
        int currentTabIndex = resolveTabPositions(mCurrentTab);
        binding.tabLayout.getTabAt(currentTabIndex).select();
    }

    private int resolveTabPositions(String currentTab) {
        if (currentTab.equals(TabMenuType.HOME.toString())) {
            return TAB_INDEX_HOME;
        } else if (currentTab.equals(TabMenuType.PREV_MEDIA.toString())) {
            return TAB_INDEX_PREV_MEDIA;
        } else if (currentTab.equals(TabMenuType.PARTICIPATE.toString())) {
            return TAB_INDEX_PARTICIPATE;
        } else if (currentTab.equals(TabMenuType.MY_PAGE.toString())) {
            return TAB_INDEX_MYPAGE;
        } else if (currentTab.equals(TabMenuType.COVERSTAR.toString())) {
            return TAB_INDEX_COVERSTAR;
        }  else {
            return -1;
        }
    }

    @Override
    public void onBackPressed() {
        int stackValue = 0;

        if (tagStacks.get(mCurrentTab).size() == 1) {
            Stack<String> value = tagStacks.get(stackList.get(1));
            if (value.size() > 1) {
                stackValue = value.size();
                popAndNavigateToPreviousMenu();
            }
            if (stackValue <= 1) {
                if (menuStacks.size() > 1) {
                    navigateToPreviousMenu();
                } else {
                    if (menuStacks.get(0).equals(TabMenuType.HOME.toString())) {
                        BackClickEventHandler.onBackPressed(this);
                    } else {
                        menuStacks.remove(0);
                        selectedTab(TabMenuType.HOME.toString());
                        setCurrentTabSelected();
                    }
                }
            }
        } else {
            BaseFragment targetFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tagStacks.get(mCurrentTab).lastElement());
            targetFragment.onBackPressed();
        }
    }

    private void popAndNavigateToPreviousMenu() {
        String tempCurrent = stackList.get(0);
        mCurrentTab = stackList.get(1);

        showPreviousMenu();

        FragmentUtils.updateStackToIndexFirst(stackList, tempCurrent);
        menuStacks.remove(0);
    }

    private void navigateToPreviousMenu() {
        menuStacks.remove(0);
        mCurrentTab = menuStacks.get(0);
        showPreviousMenu();
    }

    private void showPreviousMenu() {
        setCurrentTabSelected();

        Fragment targetFragment = getSupportFragmentManager().findFragmentByTag(tagStacks.get(mCurrentTab).lastElement());
        if (targetFragment != null) {
            FragmentUtils.showHideTabFragment(getSupportFragmentManager(), targetFragment, getCurrentFragment());
            assignCurrentFragment((BaseFragment) targetFragment);
        }
    }

    public void removeTargetFragment(Fragment target) {
        Stack<String> tagStack = tagStacks.get(mCurrentTab);
        if (tagStack != null) {
            if (tagStack.remove(FragmentUtils.getFragmentKey(target))) {
                FragmentUtils.removeTargetFragment(getSupportFragmentManager(), target);
            }
        }
    }

    public void onFragmentInteractionCallback(BaseFragment fragment) {
        FragmentUtils.addShowHideFragment(getSupportFragmentManager(), tagStacks, MainActivity.mCurrentTab, fragment,
                getCurrentFragmentFromShownStack(), R.id.mainContainer, true);
        assignCurrentFragment(fragment);
    }

    public void popFragment() {
        String fragmentTag = tagStacks.get(mCurrentTab).elementAt(tagStacks.get(mCurrentTab).size() - 2);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            tagStacks.get(mCurrentTab).pop();
            FragmentUtils.removeFragment(getSupportFragmentManager(), fragment, getCurrentFragment());
            assignCurrentFragment((BaseFragment) fragment);
        }
    }

    public void setTabLayoutVisibility(boolean isVisible) {
        if (isVisible) {
            if (binding.tabLayout.getVisibility() != View.VISIBLE) {
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.line.setVisibility(View.VISIBLE);
            }
        } else {
            binding.tabLayout.setVisibility(View.GONE);
            binding.line.setVisibility(View.GONE);
        }
    }

    private void addShowHideFragment(Fragment fragment, String tabName, boolean shouldAddToStack, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentUtils.addShowHideFragment(
                getSupportFragmentManager(),
                tagStacks,
                tabName,
                fragment,
                getCurrentFragmentFromShownStack(),
                R.id.mainContainer,
                shouldAddToStack
        );

        assignCurrentFragment((BaseFragment) fragment);
    }

    private Fragment getCurrentFragmentFromShownStack() {
        return getSupportFragmentManager().findFragmentByTag(
                tagStacks.get(mCurrentTab).elementAt(tagStacks.get(mCurrentTab).size() - 1));
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    protected void assignCurrentFragment(BaseFragment current) {
        currentFragment = current;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatRoomListHelper.getSingleInstance().initChattingListListener();
    }
}