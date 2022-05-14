package com.shinleeholdings.coverstar.util;

public class ContestManager {

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