package com.shinleeholdings.coverstar.util;

import com.shinleeholdings.coverstar.data.ContestData;

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
}