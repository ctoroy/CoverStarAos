package util

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.shinleeholdings.coverstar.R
import com.truefriend.ministock.R
import com.truefriend.ministock.ui.CustomWebViewFragment
import com.truefriend.ministock.ui.autoinvest.AutoInvestDetailFragment
import com.truefriend.ministock.ui.autoinvest.AutoInvestHistoryFragment
import com.truefriend.ministock.ui.bank.BankTransListFragment
import com.truefriend.ministock.ui.calculate.CalculatorFragment
import com.truefriend.ministock.ui.calculate.LiveCalculatorFragment
import com.truefriend.ministock.ui.etc.ThemeDetailFragment
import com.truefriend.ministock.ui.myasset.DpsHistoryFragment
import com.truefriend.ministock.ui.myasset.StockOrderHistoryFragment
import com.truefriend.ministock.ui.order.BasketFragment
import com.truefriend.ministock.ui.order.OrderCompleteFragment
import com.truefriend.ministock.ui.ranking.RankingContentsDetailFragment
import com.truefriend.ministock.ui.setting.*
import com.truefriend.ministock.ui.stock.HasStockListFragment
import com.truefriend.ministock.ui.stock.PflsListFragment
import com.truefriend.ministock.ui.stock.StockInfoFragment
import com.truefriend.ministock.ui.stock.TagStockListFragment
import com.truefriend.ministock.ui.widget.MainBaseFragment
import java.util.*

class FragmentUtils {

    companion object {
        private val TAG_SEPARATOR = ":"

        val TABNAME = ".TABNAME"
        val SHOULD_ADD = ".SHOULD_ADD"

        fun isTabLayoutGoneFragment(fragment: MainBaseFragment): Boolean {
            try {
                val packageName = fragment.javaClass.`package`?.name
                // 미니톡 프래그먼트들은 탭레이아웃이 보여지지 않는 프래그먼트
                if (TextUtils.isEmpty(packageName) == false && packageName!!.contains("minitalk")) {
                    return true
                }

                when (fragment) {
                    is BasketFragment,
                    is OrderCompleteFragment,
                    is CustomWebViewFragment,
                    is CalculatorFragment,
                    is LiveCalculatorFragment-> return true
                }
            } catch (e: Exception) {
            }

            return false
        }

        private fun setAnimation(fragmentTransition: FragmentTransaction) {
            fragmentTransition.setCustomAnimations(
                R.anim.fragment_fade_in,
                R.anim.fragment_fade_out
            )
        }

        fun getFragmentKey(fragment: Fragment) : String {
            return fragment::class.simpleName + TAG_SEPARATOR + fragment.hashCode()
        }

        fun addInitialTabFragment(
            fragmentManager: FragmentManager,
            tagStacks: Map<String, Stack<String>>,
            tag: String,
            fragment: Fragment,
            layoutId: Int,
            shouldAddToStack: Boolean
        ) {
            val fragmentKey = getFragmentKey(fragment)
            val fragmentTransition = fragmentManager.beginTransaction()
            setAnimation(fragmentTransition)

            fragmentTransition.add(layoutId, fragment, fragmentKey).commitAllowingStateLoss()

            if (shouldAddToStack) {
                tagStacks[tag]!!.push(fragmentKey)
            }
        }

        fun addAdditionalTabFragment(
            fragmentManager: FragmentManager,
            tagStacks: Map<String, Stack<String>>,
            tag: String,
            show: Fragment,
            hideFragment: Fragment?,
            layoutId: Int,
            shouldAddToStack: Boolean
        ) {
            val fragmentKey = getFragmentKey(show)
            val fragmentTransition = fragmentManager.beginTransaction()
            setAnimation(fragmentTransition)

            fragmentTransition.add(layoutId, show, fragmentKey).show(show)

            hideFragment?.let {
                fragmentTransition.hide(it)
            }

            fragmentTransition.commitAllowingStateLoss()

            if (shouldAddToStack) {
                tagStacks[tag]!!.push(fragmentKey)
            }
        }

        fun addShowHideFragment(
            fragmentManager: FragmentManager,
            tagStacks: Map<String, Stack<String>>,
            tag: String,
            show: Fragment,
            hide: Fragment?,
            layoutId: Int,
            shouldAddToStack: Boolean
        ) {
            val fragmentKey = getFragmentKey(show)
            val fragmentTransition = fragmentManager.beginTransaction()
            setAnimation(fragmentTransition)

            fragmentTransition.add(layoutId, show, fragmentKey).show(show)

            hide?.let {
                fragmentTransition.hide(it)
            }

            fragmentTransition.commitAllowingStateLoss()

            if (shouldAddToStack) {
                tagStacks[tag]!!.push(fragmentKey)
            }
        }

        fun showHideTabFragment(fragmentManager: FragmentManager, show: Fragment, hide: Fragment?) {
            val fragmentTransition = fragmentManager.beginTransaction()
            setAnimation(fragmentTransition)

            hide?.let {
                fragmentTransition.hide(it)
            }
            fragmentTransition.show(show).commitAllowingStateLoss()
        }

        fun removeFragment(
            fragmentManager: FragmentManager,
            show: Fragment,
            removeFragment: Fragment?
        ) {
            val fragmentTransition = fragmentManager.beginTransaction()
            setAnimation(fragmentTransition)

            removeFragment?.let {
                fragmentTransition.remove(it)
            }
            fragmentTransition.show(show).commitAllowingStateLoss()
        }

        fun removeTargetFragment(
                fragmentManager: FragmentManager,
                removeFragment: Fragment
        ) {
            val fragmentTransition = fragmentManager.beginTransaction()
            fragmentTransition.remove(removeFragment).commitAllowingStateLoss()
        }

        fun startFragment(
            targetFragment: MainBaseFragment,
            stackTabName: String,
            shouldAdd: Boolean,
            fragmentInteractionCallback: MainBaseFragment.FragmentInteractionCallback?
        ) {
            fragmentInteractionCallback?.let {
                val bundle = Bundle()
                bundle.putString(TABNAME, stackTabName)
                bundle.putBoolean(SHOULD_ADD, shouldAdd)
                fragmentInteractionCallback.onFragmentInteractionCallback(targetFragment, bundle)
            }
        }

        fun getFragmentInstance(fragmentName: String): MainBaseFragment? = when (fragmentName) {
            StockInfoFragment::class.simpleName -> StockInfoFragment()
            TagStockListFragment::class.simpleName -> TagStockListFragment()
            ThemeDetailFragment::class.simpleName -> ThemeDetailFragment()
            StockOrderHistoryFragment::class.simpleName -> StockOrderHistoryFragment()
            HasStockListFragment::class.simpleName -> HasStockListFragment()
            PflsListFragment::class.simpleName -> PflsListFragment()
            BankTransListFragment::class.simpleName -> BankTransListFragment()
            TimeLineListFragment::class.simpleName -> TimeLineListFragment()
            NoticeListFragment::class.simpleName -> NoticeListFragment()
            NoticeDetailFragment::class.simpleName -> NoticeDetailFragment()
            FaqListFragment::class.simpleName -> FaqListFragment()
            AuthAndSecurityFragment::class.simpleName -> AuthAndSecurityFragment()
            AuthManageFragment::class.simpleName -> AuthManageFragment()
            OtpManageFragment::class.simpleName -> OtpManageFragment()
            SettingFragment::class.simpleName -> SettingFragment()
            DpsHistoryFragment::class.simpleName -> DpsHistoryFragment()
            RankingContentsDetailFragment::class.simpleName -> RankingContentsDetailFragment()
            AutoInvestDetailFragment::class.simpleName -> AutoInvestDetailFragment()
            AutoInvestHistoryFragment::class.simpleName -> AutoInvestHistoryFragment()
            CustomWebViewFragment::class.simpleName -> CustomWebViewFragment()
            NoticeDetailFragment::class.java.simpleName -> NoticeDetailFragment()
            BasketFragment::class.java.simpleName -> BasketFragment()
            else -> null
        }
    }
}
