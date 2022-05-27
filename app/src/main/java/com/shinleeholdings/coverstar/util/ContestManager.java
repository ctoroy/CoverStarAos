package com.shinleeholdings.coverstar.util;

import com.shinleeholdings.coverstar.data.ContestData;

import java.util.ArrayList;

public class ContestManager {

	private static volatile ContestManager instance;
	private final static Object lockObject = new Object();

	public interface IContestInfoUpdateListener {
		void onWatchCountUpdated(ContestData item);
		void onVoteCountUpdated(ContestData item);
	}
	private final ArrayList<IContestInfoUpdateListener> updateListenerList = new ArrayList<>();

	public void addInfoChangeListener(IContestInfoUpdateListener listener) {
		synchronized(updateListenerList) {
			if (updateListenerList.contains(listener) == false) {
				updateListenerList.add(listener);
			}
		}
	}

	public void removeInfoChangeListener(IContestInfoUpdateListener listener) {
		synchronized(updateListenerList) {
			updateListenerList.remove(listener);
		}
	}

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

    public void sendWatchCountUpdateEvent(ContestData item) {
		synchronized(updateListenerList) {
			if (updateListenerList.size() == 0) {
				return;
			}

			for (int i = 0; i< updateListenerList.size(); i++) {
				updateListenerList.get(i).onWatchCountUpdated(item);
			}
		}
    }

	public void sendVoteCompleteEvent(ContestData item) {
		synchronized(updateListenerList) {
			if (updateListenerList.size() == 0) {
				return;
			}

			for (int i = 0; i< updateListenerList.size(); i++) {
				updateListenerList.get(i).onVoteCountUpdated(item);
			}
		}
	}
}