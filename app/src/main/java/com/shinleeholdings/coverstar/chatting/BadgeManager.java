package com.shinleeholdings.coverstar.chatting;

import android.view.View;
import android.widget.TextView;

import com.shinleeholdings.coverstar.MyApplication;

import java.util.ArrayList;
import java.util.Hashtable;

public class BadgeManager {
    private static final String TAG = "BadgeManager";
    private static volatile BadgeManager instance;
    private final static Object lockObject = new Object();

    private long chattingBadgeCount;
    private final Hashtable<String, Long> chattingBadgeHashTable = new Hashtable<>();
    private final ArrayList<IBadgeCountChangeEventListener> badgeCountEventListeners = new ArrayList<IBadgeCountChangeEventListener>();

    /**
     * 뱃지 개수 정보가 변경될 경우 이벤트 처리
     *
     * @author SEODK
     *
     */
    public interface IBadgeCountChangeEventListener {
        public void onBadgeCountChanged();
    }

    public void addBadgeCountChangeEventListener(IBadgeCountChangeEventListener listener) {
        synchronized (this) {
            if (badgeCountEventListeners.contains(listener)) {
                return;
            }
            badgeCountEventListeners.add(listener);
        }
    }

    public void removeBadgeCountChangeEventListener(IBadgeCountChangeEventListener listener) {
        synchronized (this) {
            if (badgeCountEventListeners.size() == 0) {
                return;
            }
            badgeCountEventListeners.remove(listener);
        }
    }

    public static BadgeManager getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new BadgeManager();
                }
            }
        }

        return instance;
    }

    public void setBadgeView(TextView badgeView, int badgeCount) {
        if (badgeView == null) {
            return;
        }

        if (badgeCount <= 0) {
            badgeView.setVisibility(View.GONE);
        } else {
            String value = "";
            if (badgeCount > 999) {
                value = "999+";
            } else {
                value = String.valueOf(badgeCount);
            }
            badgeView.setText(value);
            badgeView.setVisibility(View.VISIBLE);
        }
    }

    public void initBadgeInfo() {
        chattingBadgeHashTable.clear();
        chattingBadgeCount = 0;
    }

    public long getChattingRoomBadge(String chattingId) {
        boolean hasItem = chattingBadgeHashTable.containsKey(chattingId);

        if (hasItem) {
            return chattingBadgeHashTable.get(chattingId);
        }

        return 0;
    }

    public long getChattingBadgeCount() {
        return chattingBadgeCount;
    }

    public void setBadgeInfo(String chatId, long value) {
        chattingBadgeHashTable.put(chatId, value);
        chattingBadgeCount = chattingBadgeCount + value;
        sendBadgeCountChangeEvent();
    }

    public void updateBadgeInfo(String chatId, long value) {
        if (chattingBadgeHashTable.containsKey(chatId)) {
            long count = chattingBadgeHashTable.get(chatId);
            chattingBadgeHashTable.put(chatId, value);

            chattingBadgeCount = chattingBadgeCount - count + value;
            sendBadgeCountChangeEvent();
        }
    }

    public void deleteBadgeInfo(String chatId) {
        if (chattingBadgeHashTable.containsKey(chatId)) {
            long count = chattingBadgeHashTable.get(chatId);
            chattingBadgeHashTable.remove(chatId);

            chattingBadgeCount = chattingBadgeCount - count;
            sendBadgeCountChangeEvent();
        }
    }

    public void sendBadgeCountChangeEvent() {
        synchronized (this) {

            try {
                if (badgeCountEventListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {

                    @Override
                    public void run() {
                        for (IBadgeCountChangeEventListener listener : badgeCountEventListeners) {
                            if (listener != null) {
                                listener.onBadgeCountChanged();
                            }
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }
}
