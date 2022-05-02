package com.shinleeholdings.coverstar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.tabs.TabLayout;
import com.shinleeholdings.coverstar.databinding.ActivityMainBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;

import java.util.Stack;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private enum TabMenuType(int iconResId, int textResId) {
        HOME(R.drawable.icon_outlined_directions_chevron_down, R.string.app_name),
        HOME2(R.drawable.icon_outlined_directions_chevron_down, R.string.app_name),
        HOME3(R.drawable.icon_outlined_directions_chevron_down, R.string.app_name),
        HOME4(R.drawable.icon_outlined_directions_chevron_down, R.string.app_name)
    }

    private var currentFragment: MainBaseFragment? = null

    // 각 탭별로 쌓여있는 프래그먼트 관리
    protected val tagStacks: Map<String, Stack<String>> by lazy {
        LinkedHashMap<String, Stack<String>>().apply {
            put(MainTabActivity.TAB_HOME, Stack<String>())
            put(MainTabActivity.TAB_SEARCH, Stack<String>())
            put(MainTabActivity.TAB_THEME, Stack<String>())
            put(MainTabActivity.TAB_MYBANK, Stack<String>())
            put(MainTabActivity.TAB_MORE, Stack<String>())
        }
    }

    protected val stackList: ArrayList<String> by lazy {
        ArrayList<String>().apply {
            add(MainTabActivity.TAB_HOME)
            add(MainTabActivity.TAB_SEARCH)
            add(MainTabActivity.TAB_THEME)
            add(MainTabActivity.TAB_MYBANK)
            add(MainTabActivity.TAB_MORE)
        }
    }

    protected val menuStacks: ArrayList<String> by lazy {
        ArrayList<String>().apply {
            add(MainTabActivity.TAB_HOME)
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 알림 리스트 작업
        // 바텀시트 메뉴 작업

        // 탭 구조로 메인 액티비티 만들기 , 프래그먼트도
            // 홈 / 지난 영상 / 참가 신청 / 마이페이지

        initUi();
    }

    private void initUi() {
        for (int i=0; i <TabMenuType.values().length; i++) {
            RelativeLayout tabMenuLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_menu_item, null);

            AppCompatImageView iconImageView = tabMenuLayout.findViewById(R.id.tab_menu_image);
            TextView tabTitleTextView = tabMenuLayout.findViewById(R.id.tab_menu_title);

            tabMenuLayout.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }

    @Override
    public void onBackPressed() {
        var stackValue = 0
        if (tagStacks[mCurrentTab]!!.size == 1) {
            val value = tagStacks[stackList[1]]
            if (value!!.size > 1) {
                stackValue = value.size
                popAndNavigateToPreviousMenu()
            }
            if (stackValue <= 1) {
                if (menuStacks.size > 1) {
                    navigateToPreviousMenu()
                } else {
                    if (menuStacks[0] == TAB_HOME) {
                        super.onBackPressed()
                    } else {
                        menuStacks.removeAt(0)
                        selectedTab(TAB_HOME)
                        setCurrentTabSelected()
                    }
                }
            }
        } else {
            val targetFragment = supportFragmentManager.findFragmentByTag(tagStacks[mCurrentTab]!!.lastElement())
            (targetFragment as? OnFragmentBackPressed)?.onBackPressed()
        }
        BackClickEventHandler.onBackPressed(this);
    }



    override fun removeTargetFragment(target: Fragment) {
        val tagStack = tagStacks[MainTabActivity.mCurrentTab]
        tagStack?.let {
            if (it.remove(FragmentUtils.getFragmentKey(target))) {
                FragmentUtils.removeTargetFragment(supportFragmentManager, target)
            }
        }
    }

    override fun popFragment() {
        val fragmentTag = tagStacks[MainTabActivity.mCurrentTab]!!.elementAt(tagStacks[MainTabActivity.mCurrentTab]!!.size - 2)
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        fragment?.let {
            tagStacks[MainTabActivity.mCurrentTab]!!.pop()
            FragmentUtils.removeFragment(supportFragmentManager, it, getCurrentFragment())
            assignCurrentFragment(it as MainBaseFragment)
        }
    }

    private fun addShowHideFragment(
            fragment: Fragment,
            tabName: String,
            shouldAddToStack: Boolean,
            bundle: Bundle?
    ) {
        CustomLogger.info("addFragment : ${fragment::class.java.simpleName}")

        bundle?.let {
            fragment.arguments = it
        }

        FragmentUtils.addShowHideFragment(
                supportFragmentManager,
                tagStacks,
                tabName,
                fragment,
                getCurrentFragmentFromShownStack(),
                R.id.mainContainer,
                shouldAddToStack
        )

        assignCurrentFragment(fragment as MainBaseFragment)
    }

    private fun getCurrentFragmentFromShownStack(): Fragment? {
        return supportFragmentManager.findFragmentByTag(
                tagStacks[MainTabActivity.mCurrentTab]!!.elementAt(
                tagStacks[MainTabActivity.mCurrentTab]!!.size - 1
            )
        )
    }

    fun getCurrentFragment(): MainBaseFragment? {
        return currentFragment
    }

    protected fun assignCurrentFragment(current: MainBaseFragment?) {
        currentFragment = current
    }

}