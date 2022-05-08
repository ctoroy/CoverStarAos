package com.shinleeholdings.coverstar.util;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.ui.fragment.BaseFragment;
import com.shinleeholdings.coverstar.ui.fragment.ContestDetailFragment;

public class ContestManager {

	public interface IContestItemClickListener {
		public void onContestClicked(ContestData contestData);
	}

	private static volatile ContestManager instance;
	private final static Object lockObject = new Object();

	public static ContestManager getSingleInstance() {
		if (instance == null) {
			synchronized (lockObject) {
				if (instance == null) {
					instance = new ContestManager();
				}
			}
		}

		return instance;
	}

	public void showContestDetailFragment(MainActivity activity, ContestData info) {
		// TODO 콘테스트 상세로 이동
		BaseFragment contestDetailFragment = new ContestDetailFragment();
		activity.onFragmentInteractionCallback(contestDetailFragment);
	}
}