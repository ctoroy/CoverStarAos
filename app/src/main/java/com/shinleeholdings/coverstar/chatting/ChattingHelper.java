package com.shinleeholdings.coverstar.chatting;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.FireBaseHelper;
import com.shinleeholdings.coverstar.util.LoginHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

// 혹시 몰라서 원본 남겨놓기 ㅋ
public class ChattingHelper {
//    private static final String TAG = "ChattingHelper";
//    private static volatile ChattingHelper instance;
//    private final static Object lockObject = new Object();
//
//    private final DataBaseHelper dbHelper;
//
//    public static final int MESSAGE_GET_COUNT = 100;
//
//    private String currentChattingId = "";
//    private ChatRoomItem currentChattingRoomInfo;
//
//    private IChattingItemEventListener onChattingMessageReceiveEventListener;
//
//    private ListenerRegistration chattingMessageShotEventListener;
//    private ListenerRegistration chattingListEventListener;
//    private ListenerRegistration chattingMemberChangeEventListener;
//
//    public static final String TB_CHAT_INFO = "chat_info";
//    public static final String TB_CHAT_LIST = "chat_list";
//    public static final String TB_CHAT_MESSAGE = "message";
//
//    public static final String TEMP_COLLECTION_NAME = "a";
//
//    public static final String FIELDNAME_BADGE_CNT = "badge_cnt";
//    public static final String FIELDNAME_CUSTOM_ROOM_NAME = "custom_room_name";
//
//    public static final String FIELDNAME_USER_ID = "user_id";
//    public static final String FIELDNAME_USER_NAME = "user_name";
//    public static final String FIELDNAME_USER_PHOTO = "user_photo";
//
//    private ArrayList<ChatRoomItem> chattingRoomList = null;
//
//    private HashMap<String, ArrayList<ChattingRoomMember>> chattingRoomMemberHashMap = new HashMap<>();
//
//    private final ArrayList<IChattingRoomEventListener> chattingRoomInfoUpdateListeners = new ArrayList<>();
//
//    private final HashMap<String, ListenerRegistration> chattingRoomDetailEventListener = new HashMap<>();
//    private final HashMap<String, ListenerRegistration> chattingRoomMemberEventListener = new HashMap<>();
//
//    private boolean chattingListLoadingCompleted = false;
//
//    public interface IChattingItemEventListener {
//        public void onItemAdded(ChattingItem item);
//
//        public void onItemChanged(ChattingItem item);
//
//        public void onItemRemoved(ChattingItem item);
//
//        public void onError(String message);
//    }
//
//    public interface IGetMessageListListener {
//        public void onMessageListLoaded(ArrayList<ChattingItem> itemList);
//    }
//
//    public interface IChattingRoomEventListener {
//        public void onChattingRoomLoadCompleted();
//
//        public void onChattingRoomDetailInfoChange(String chatId, ChatRoomItem item);
//
//        public void onChattingRoomAdded(String chatId, ChatRoomItem item);
//
//        public void onChattingRoomInfoChanged(String chatId);
//
//        public void onChattingRoomRemoved(String chatId);
//
//        public void onMemberUpdate(String chatId);
//    }
//
//    public static ChattingHelper getSingleInstance() {
//        if (instance == null) {
//            synchronized (lockObject) {
//                if (instance == null) {
//                    instance = new ChattingHelper();
//                }
//            }
//        }
//
//        return instance;
//    }
//
//    private ChattingHelper() {
//        dbHelper = DataBaseHelper.getSingleton(MyApplication.getContext());
//        createMessageQueueTable();
//    }
//
//    private void createMessageQueueTable() {
//        dbHelper.createTable(ChattingConstants.CREATE_TABLE_QUERY);
//    }
//
//    /**
//     * DB에 저장된 채팅 리스트를 불러온다.
//     *
//     * @return
//     */
//    public ArrayList<ChattingItem> getSendFailMessageList() {
//        ArrayList<ChattingItem> chattingList = new ArrayList<ChattingItem>();
//
//        String selection = String.format(ChattingConstants.CHATTINGID_SELECTION, currentChattingId);
//        Cursor cursor = dbHelper.query(ChattingConstants.MESSAGE_QUEUE_TABLE_NAME, null, selection, null, null, null, null);
//
//        if (cursor == null) {
//            return chattingList;
//        }
//
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//
//                String data = cursor.getString(ChattingConstants.INDEX_DATA);
//                if (TextUtils.isEmpty(data)) {
//                    continue;
//                }
//
//                ChattingItem.SENDSTATE sendState = ChattingItem.SENDSTATE.FAIL;
//                ChatItem chatItem;
//                try {
//                    chatItem = new ChatItem(new JSONObject(data));
//                } catch (Exception e) {
//                    DebugLogger.e("test", "loadChattingMessage error : " + e.getMessage());
//                    continue;
//                }
//
//                ChattingItem item = new ChattingItem(chatItem, currentChattingId, sendState);
//                chattingList.add(item);
//            }
//        }
//
//        cursor.close();
//
//        return chattingList;
//    }
//
//    /**
//     * 채팅 아이템을 DB에 저장한다.
//     *
//     * @param item
//     */
//    public int saveChattingMessageToDataBase(ChattingItem item) {
//        if (item == null) {
//            return -1;
//        }
//
//        String itemKey = item.getTimeStampKey();
//        String chattingId = item.getChattingId();
//        String selection = String.format(ChattingConstants.SELECTION, chattingId, itemKey);
//        String hasItemQuery = String.format(ChattingConstants.QUERY_HASITEM, chattingId, itemKey);
//
//        ContentValues values = new ContentValues();
//        values.put(ChattingConstants.FIELD_KEY, itemKey);
//        values.put(ChattingConstants.FIELD_CHATTING_ID, chattingId);
//        values.put(ChattingConstants.FIELD_DATA, item.getJsonStringData());
//
//        // DB에 저장 - 있으면 업데이트하고 없으면 add한다.
//        return dbHelper.insertQuery(ChattingConstants.MESSAGE_QUEUE_TABLE_NAME, hasItemQuery, selection, values);
//    }
//
//    /**
//     * DB에서 해당 아이템을 지운다. 전송이 실패했을 경우 해당 아이템 지우는데 사용한다.
//     *
//     * @param item
//     */
//    public int deleteChattingItem(ChattingItem item) {
//        if (item == null) {
//            return 0;
//        }
//
//        String selection = String.format(ChattingConstants.SELECTION, item.getChattingId(), item.getTimeStampKey());
//        return dbHelper.deleteQuery(ChattingConstants.MESSAGE_QUEUE_TABLE_NAME, selection);
//    }
//
//    public void addChattingRoomListListener(IChattingRoomEventListener chattingRoomListListener) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            if (chattingRoomInfoUpdateListeners.contains(chattingRoomListListener)) {
//                return;
//            }
//            chattingRoomInfoUpdateListeners.add(chattingRoomListListener);
//        }
//    }
//
//    public void removeChattingRoomListListener(IChattingRoomEventListener chattingRoomListListener) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            if (chattingRoomInfoUpdateListeners.size() == 0) {
//                return;
//            }
//            chattingRoomInfoUpdateListeners.remove(chattingRoomListListener);
//        }
//    }
//
//    private void sendChattingRoomAddedEvent(final String chatId, final ChatRoomItem item) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            try {
//                if (chattingRoomInfoUpdateListeners.size() == 0) {
//                    return;
//                }
//
//                MyApplication.getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
//                                if (listener != null) {
//                                    listener.onChattingRoomAdded(chatId, item);
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void sendChattingRoomListLoadCompleteEvent() {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            try {
//                if (chattingRoomInfoUpdateListeners.size() == 0) {
//                    return;
//                }
//
//                MyApplication.getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
//                                if (listener != null) {
//                                    listener.onChattingRoomLoadCompleted();
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void sendChattingRoomDetailInfoEvent(final String chatId, final ChatRoomItem item) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            try {
//                if (chattingRoomInfoUpdateListeners.size() == 0) {
//                    return;
//                }
//
//                MyApplication.getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
//                                if (listener != null) {
//                                    listener.onChattingRoomDetailInfoChange(chatId, item);
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void sendChattingRoomInfoChangeEvent(final String chatId) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            try {
//                if (chattingRoomInfoUpdateListeners.size() == 0) {
//                    return;
//                }
//
//                MyApplication.getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
//                                if (listener != null) {
//                                    listener.onChattingRoomInfoChanged(chatId);
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void sendChattingMemberUpdateEvent(final String chatId) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            try {
//                if (chattingRoomInfoUpdateListeners.size() == 0) {
//                    return;
//                }
//
//                MyApplication.getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
//                                if (listener != null) {
//                                    listener.onMemberUpdate(chatId);
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void sendChattingRoomRemoveEvent(final String chatId) {
//        synchronized (chattingRoomInfoUpdateListeners) {
//            try {
//                if (chattingRoomInfoUpdateListeners.size() == 0) {
//                    return;
//                }
//
//                MyApplication.getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
//                                if (listener != null) {
//                                    listener.onChattingRoomRemoved(chatId);
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    public void setCurrentChattingId(String chattingId) {
//        currentChattingId = chattingId;
//    }
//
//    public String getCurrentChattingId() {
//        return currentChattingId;
//    }
//
//    public void initChattingListListener() {
//        if (chattingListEventListener != null) {
//            chattingListEventListener.remove();
//        }
//        removeRegisteredChattingRoomDetailInfo();
//        removeRegisteredChattingMemberEventListener();
//    }
//
//    public ArrayList<ChatRoomItem> getChattingRoomList() {
//        return chattingRoomList;
//    }
//
//    public ChatRoomItem getChattingRoomInfo(String chatId) {
//        if (TextUtils.isEmpty(chatId) || chattingRoomList == null || chattingRoomList.size() <= 0) {
//            return null;
//        }
//
//        for (ChatRoomItem item : chattingRoomList) {
//            if (item.getChatId().equals(chatId)) {
//                return item;
//            }
//        }
//
//        return null;
//    }
//
//    private CollectionReference getChatListCollectionRef() {
//        return FireBaseHelper.getSingleInstance().getDatabase()
//                .collection(TB_CHAT_LIST)
//                .document(LoginHelper.getSingleInstance().getLoginUserId())
//                .collection(TEMP_COLLECTION_NAME);
//    }
//
//    private void addChattingRoomItem(String chatId, Map<String, Object> data, boolean isAdded) {
//        DebugLogger.e("test", "TB_CHAT_LIST addChattingRoomItem chatId : " + chatId + " , data : " + data);
//        long badgeCount = (Long) data.get(FIELDNAME_BADGE_CNT);
//        String customName = (String) data.get(FIELDNAME_CUSTOM_ROOM_NAME);
//
//        ChatRoomItem item = new ChatRoomItem();
//        item.setChatId(chatId);
//        item.setCustomRoomName(customName);
//
//        BadgeManager.getSingleInstance().setBadgeInfo(chatId, badgeCount);
//        chattingRoomList.add(item);
//
//        if (isAdded) {
//            sendChattingRoomAddedEvent(chatId,item);
//        }
//
//        getChattingRoomDetailInfo(item);
//    }
//
//    public boolean isChattingListLoadingCompleted() {
//        return chattingListLoadingCompleted;
//    }
//
//    public void getChattingRoomListInfo() {
//        BadgeManager.getSingleInstance().initBadgeInfo();
//        removeRegisteredChattingRoomDetailInfo();
//        removeRegisteredChattingMemberEventListener();
//        chattingRoomList = null;
//        chattingRoomMemberHashMap.clear();
//        chattingListLoadingCompleted = false;
//        if (chattingListEventListener != null) {
//            chattingListEventListener.remove();
//        }
//
//        getChatListCollectionRef().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful() == false) {
//                    return;
//                }
//
//                if (chattingRoomList == null) {
//                    chattingRoomList = new ArrayList<>();
//                }
//
//                DebugLogger.e("test", "TB_CHAT_LIST getChattingRoomListInfo success");
//                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
//                    String chatId = doc.getId();
//                    Map<String, Object> data = doc.getData();
//                    addChattingRoomItem(chatId, data, false);
//                }
//                chattingListLoadingCompleted = true;
//                sendChattingRoomListLoadCompleteEvent();
//                chattingListEventListener = getChatListCollectionRef().addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        if (queryDocumentSnapshots == null) {
//                            return;
//                        }
//
//                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                            QueryDocumentSnapshot documentSnapshot = dc.getDocument();
//                            String chatId = documentSnapshot.getId();
//
//                            Map<String, Object> data = dc.getDocument().getData();
//                            DebugLogger.e("test", "TB_CHAT_LIST chatId : " + chatId + ", Type : " + dc.getType() + " , data : " + data);
//
//                            switch (dc.getType()) {
//                                case ADDED:
//                                    if (chattingRoomList == null) {
//                                        chattingRoomList = new ArrayList<>();
//                                    }
//
//                                    for (ChatRoomItem chatRoomItem : chattingRoomList) {
//                                        if (chatId.equals(chatRoomItem.getChatId())) {
//                                            return;
//                                        }
//                                    }
//
//                                    addChattingRoomItem(chatId, data, true);
//                                    break;
//                                case MODIFIED:
//                                    long badgeCount = (Long) data.get(FIELDNAME_BADGE_CNT);
//                                    String customName = (String) data.get(FIELDNAME_CUSTOM_ROOM_NAME);
//
//                                    for (ChatRoomItem chattingItem : chattingRoomList) {
//                                        if (chattingItem.getChatId().equals(chatId)) {
//                                            chattingItem.setCustomRoomName(customName);
//                                            break;
//                                        }
//                                    }
//
//                                    if (chatId.equals(ChattingHelper.getSingleInstance().getCurrentChattingId())) {
//                                        badgeCount = 0;
//                                        ChattingHelper.getSingleInstance().updateChatListInfo(chatId, ChattingHelper.FIELDNAME_BADGE_CNT, badgeCount);
//                                    }
//
//                                    BadgeManager.getSingleInstance().updateBadgeInfo(chatId, badgeCount);
//                                    sendChattingRoomInfoChangeEvent(chatId);
//                                    break;
//                                case REMOVED:
//                                    for (ChatRoomItem chattingItem : chattingRoomList) {
//                                        if (chattingItem.getChatId().equals(chatId)) {
//                                            chattingRoomList.remove(chattingItem);
//                                            break;
//                                        }
//                                    }
//                                    removeChattingRoomDetailInfoListener(chatId);
//                                    chattingRoomMemberHashMap.remove(chatId);
//                                    removeRegisteredChattingMemberEventListener(chatId);
//                                    BadgeManager.getSingleInstance().deleteBadgeInfo(chatId);
//                                    sendChattingRoomRemoveEvent(chatId);
//
//                                    break;
//                            }
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    public CollectionReference getChatInfoDocuRef() {
//        return FireBaseHelper.getSingleInstance().getDatabase().collection(TB_CHAT_INFO);
//    }
//
//    private void getChattingRoomDetailInfo(final ChatRoomItem chattingItem) {
//        final String chatId = chattingItem.getChatId();
//        DebugLogger.e(TAG, "getChattingRoomDetailInfo chattingId : " + chatId);
//
//        CollectionReference docuRef = getChatInfoDocuRef();
//        getChattingRoomMemberInfo(chatId, docuRef);
//
//        ListenerRegistration listener = docuRef.document(chatId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapShot, @Nullable FirebaseFirestoreException e) {
//                DebugLogger.i(TAG, "getChattingRoomDetailInfo onComplete getResult chatId : " + chatId + ",  data : " + snapShot);
//                chattingItem.setLastMessage((String) snapShot.get("last_message"));
//                chattingItem.setMessageDate((String) snapShot.get("mdate"));
//                chattingItem.setRoomName((String) snapShot.get("room_name"));
//
//                try {
//                    synchronized (instance) {
//                        String lastMessageKey = (String) snapShot.get("last_message_key");
//                        chattingItem.setLastMessageKey(lastMessageKey);
//
//                        ArrayList<String> deleteInfoList = (ArrayList<String>) snapShot.get("last_message_delete");
//                        chattingItem.setDeleteInfoList(deleteInfoList);
//
//                        DebugLogger.i("test", "lastMessageTest getChattingRoomDetailInfo lastMessageKey : " + lastMessageKey);
//                        DebugLogger.i("test", "lastMessageTest getChattingRoomDetailInfo deleteInfoList : " + deleteInfoList);
//
//                        if (deleteInfoList != null && deleteInfoList.size() > 0) {
//                            String loginUserSeq = LoginHelper.getSingleInstance().getLoginUserId();
//                            for (String userSeq : deleteInfoList) {
//                                if (userSeq.equals(loginUserSeq)) {
//                                    DebugLogger.e("test", "lastMessageTest getChattingRoomDetailInfo deleteInfoList has mySeq");
//                                    // 삭제한 사용자 목록에 내 아이디가 있을 경우
//                                    chattingItem.setLastMessage("삭제한 메세지 입니다.");
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e1) {
//                    DebugLogger.i("test", "lastMessageTest getChattingRoomDetailInfo lastMessageInfo Error : " + e.getMessage());
//                }
//
//                sendChattingRoomDetailInfoEvent(chatId,chattingItem);
//            }
//        });
//
//        chattingRoomDetailEventListener.put(chatId, listener);
//    }
//
//    public ArrayList<ChattingRoomMember> getChattingRoomMember(String chatId) {
//        return chattingRoomMemberHashMap.get(chatId);
//    }
//
//    private void getChattingRoomMemberInfo(final String chatId, CollectionReference docuRef) {
//        DebugLogger.i(TAG, "getChattingRoomMemberInfo chatId : " + chatId);
//        ListenerRegistration listener = docuRef.document(chatId)
//                .collection("room_member")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        try {
//                            ArrayList<ChattingRoomMember> hashedMemberList = getChattingRoomMember(chatId);
//                            if (hashedMemberList == null) {
//                                hashedMemberList = new ArrayList<>();
//                                chattingRoomMemberHashMap.put(chatId, hashedMemberList);
//                            }
//
//                            if (queryDocumentSnapshots != null) {
//                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                                    Map<String, Object> data = dc.getDocument().getData();
//                                    DebugLogger.i(TAG, "getChattingRoomMemberInfo chatId : " + chatId + " type : " + dc.getType() + ", userName : " + data);
//                                    String userId = (String) data.get(FIELDNAME_USER_ID);
//
//                                    if (dc.getType() == DocumentChange.Type.ADDED) {
//
//                                        boolean alreadyHasMember = false;
//                                        for (ChattingRoomMember hasMember : hashedMemberList) {
//                                            if (hasMember.getUserId().equals(userId)) {
//                                                hasMember.setUserNickName((String) data.get(FIELDNAME_USER_NAME));
//                                                hasMember.setUserPhoto((String) data.get(FIELDNAME_USER_PHOTO));
//                                                alreadyHasMember = true;
//                                                break;
//                                            }
//                                        }
//                                        if (alreadyHasMember == false) {
//                                            ChattingRoomMember member = new ChattingRoomMember();
//                                            member.setUserId(userId);
//                                            member.setUserNickName((String) data.get(FIELDNAME_USER_NAME));
//                                            member.setUserPhoto((String) data.get(FIELDNAME_USER_PHOTO));
//                                            hashedMemberList.add(member);
//                                        }
//                                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
//                                        for (ChattingRoomMember member : hashedMemberList) {
//                                            if (member.getUserId().equals(userId)) {
//                                                hashedMemberList.remove(member);
//                                                break;
//                                            }
//                                        }
//                                    } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
//                                        for (ChattingRoomMember member : hashedMemberList) {
//                                            if (member.getUserId().equals(userId)) {
//                                                member.setUserNickName((String) data.get(FIELDNAME_USER_NAME));
//                                                member.setUserPhoto((String) data.get(FIELDNAME_USER_PHOTO));
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            sendChattingMemberUpdateEvent(chatId);
//                        } catch (Exception exception) {
//                        }
//                    }
//                });
//
//        chattingRoomMemberEventListener.put(chatId, listener);
//    }
//
//    public void updateChatListInfo(String chatId, String key, Object value) {
//        HashMap<String, Object> valueMap = new HashMap<>();
//        valueMap.put(key, value);
//        getChatListCollectionRef().document(chatId).update(valueMap);
//    }
//
//    // TODO 채팅방 시작
////    public void startChat(final Activity activity, ArrayList<CoverStarUser> selectedUserList, String friendsNames, final RetroCallback<ChatCreate> callback) {
////        if (selectedUserList == null || selectedUserList.size() <= 0) {
////            return;
////        }
////
////        String mySeq = LoginHelper.getSingleInstance().getLoginUserId();
////
////        StringBuilder sb = new StringBuilder();
////        sb.append(mySeq);
////        sb.append(",");
////
////        for (int i = 0; i < selectedUserList.size(); i++) {
////            HubTalkUser user = selectedUserList.get(i);
////            if (mySeq.equals(user.getSeq() + "")) {
////                continue;
////            }
////            sb.append(user.getSeq());
////
////            if (i < selectedUserList.size() - 1) {
////                sb.append(",");
////            }
////        }
////
////        startChat(activity, sb.toString(), friendsNames, callback);
////    }
//
//    // TODO 채팅방 시작
////    public void startChat(final Activity activity, HubTalkUser userData) {
////        String mySeq = LoginHelper.getSingleInstance().getLoginUserSeq();
////        int targetUserSeq = userData.getSeq();
////        if (mySeq.equals(targetUserSeq + "")) {
////            return;
////        }
////        String friendsNames = userData.getUserName() + "님";
////        startChat(activity, mySeq + "," + targetUserSeq, friendsNames, null);
////    }
//
//    // TODO 채팅방 시작
////    public void startChat(final Activity activity, String userSeq, final String friendsNames, final RetroCallback<ChatCreate> callback) {
////        HashMap<String, String> bodyProperty = new HashMap<String, String>();
////        bodyProperty.put("users", userSeq);
////
////        DebugLogger.i("test", "startChat users : " + userSeq);
////
////        ProgressDialogHelper.show(activity);
////        RetroClient.getApiInterface().createChattingRoom(bodyProperty).enqueue(new RetroCallback<ChatCreate>() {
////            @Override
////            public void onSuccess(BaseResponse receivedData) {
////                ProgressDialogHelper.dismiss();
////                ChatCreate result = (ChatCreate) receivedData;
////                String chattingRoomId = result.getChattingRoomId();
////                startChatActivity(activity, chattingRoomId);
////
////                String resultMessage = receivedData.getMessage();
////                String alreadyMessage = "이미 있는 방입니다.";
////                if (alreadyMessage.equals(resultMessage) == false) {
////                    String message = String.format(activity.getString(R.string.invite_message), LoginHelper.getSingleInstance().getLoginUser().getUserName(), friendsNames);
////                    sendMemberChangeMessage(chattingRoomId, message, ChattingHelper.MSG_TYPE_MEMBER_ENTER);
////                }
////
////                if (callback != null) {
////                    callback.onSuccess(receivedData);
////                }
////            }
////
////            @Override
////            public void onFailure(BaseResponse response) {
////                ProgressDialogHelper.dismiss();
////                if (callback != null) {
////                    callback.onFailure(response);
////                }
////            }
////        });
////    }
//
//    // TODO 채팅방 시작
////    public void startChatActivity(Activity activity, String chattingId) {
////        Intent intent = new Intent(activity, ChatActivity.class);
////        intent.putExtra(AppConstants.EXTRA.CHAT_ID, chattingId);
////        activity.startActivity(intent);
////    }
//
//    public void updateLastMessageDeleteText(final String chattingId, String messageKey, boolean force) {
//        DebugLogger.i("test", "lastMessageTest updateLastMessageDeleteText chattingId : " + chattingId + ", messageKey : " + messageKey);
//        // 삭제하는 메세지와 chat_info의 채팅방 정보에 있는 last_message_key값을 비교해서 같은 키값이면 delete에 내 아이디 추가
//        if (TextUtils.isEmpty(chattingId)) {
//            return;
//        }
//
//        if (force == false && TextUtils.isEmpty(messageKey)) {
//            return;
//        }
//
//        for (ChatRoomItem item : chattingRoomList) {
//            if (item.getChatId().equals(chattingId) == false) {
//                continue;
//            }
//
//            if (force || (TextUtils.isEmpty(item.getLastMessageKey()) == false && item.getLastMessageKey().equals(messageKey))) {
//                final DocumentReference sfDocRef = getChatInfoDocuRef().document(chattingId);
//                FireBaseHelper.getSingleInstance().getDatabase().runTransaction(new Transaction.Function<Void>() {
//                    @Override
//                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
//                        DocumentSnapshot snapShot = transaction.get(sfDocRef);
//                        try {
//                            if (force == false) {
//                                String lastMessageKey = (String) snapShot.get("last_message_key");
//                                if (lastMessageKey.equals(messageKey) == false) {
//                                    return null;
//                                }
//                            }
//
//                            DebugLogger.i("test", "lastMessageTest updateLastMessageDeleteText add myId");
//                            ArrayList<String> isDelete = (ArrayList<String>) snapShot.get("last_message_delete");
//                            if (isDelete == null) {
//                                isDelete = new ArrayList<>();
//                            }
//                            String loginUserSeq = LoginHelper.getSingleInstance().getLoginUserId();
//                            if (isDelete.contains(loginUserSeq) == false) {
//                                isDelete.add(LoginHelper.getSingleInstance().getLoginUserId());
//                                sfDocRef.update("last_message_delete", isDelete);
//                            }
//                        } catch (Exception e1) {
//                            DebugLogger.i("test", "lastMessageTest updateLastMessageDeleteText add myId error : " + e1.getMessage());
//                        }
//                        return null;
//                    }
//                });
//                break;
//            }
//        }
//    }
//
//    // TODO 채팅 메세지 보내기
////    public void sendChattingMessageToServer(final String chattingId, final ChattingItem item, final RetroCallback<BaseResponse> callbackListener) {
////        ChatItem chatItem = item.getChatItem();
////        HashMap<String, String> bodyProperty = new HashMap<String, String>();
////        bodyProperty.put("chat_id", chattingId);
////        bodyProperty.put("msg", chatItem.msg);
////        bodyProperty.put("msg_type", chatItem.msg_type);
////        bodyProperty.put("user_id", LoginHelper.getSingleInstance().getLoginUserId());
////        bodyProperty.put("keyx", item.getTimeStampKey() + "");
////        bodyProperty.put("fileSize", chatItem.fileSize + "");
////        bodyProperty.put("expireDate", chatItem.expireDate + "");
////        bodyProperty.put("width", chatItem.width);
////        bodyProperty.put("height", chatItem.height);
////        if (TextUtils.isEmpty(chatItem.filename) == false) {
////            bodyProperty.put("filename", chatItem.filename);
////        }
////
////        StringBuilder sb = new StringBuilder();
////        for (String userId : item.getChatItem().not_read) {
////            sb.append(userId);
////            sb.append(",");
////        }
////        bodyProperty.put("users", sb.toString().substring(0, sb.length() - 1));
////
////        RetroClient.getApiInterface().sendChattingMessage(bodyProperty).enqueue(new RetroCallback<BaseResponse>() {
////            @Override
////            public void onSuccess(BaseResponse receivedData) {
////                sendLastMessage(chattingId, chatItem.msg, item.getTimeStampKey() + "", Util.getCurrentTimeToCommonFormat());
////                if (callbackListener != null) {
////                    callbackListener.onSuccess(receivedData);
////                }
////            }
////
////            @Override
////            public void onFailure(BaseResponse response) {
////                if (callbackListener != null) {
////                    callbackListener.onFailure(response);
////                }
////            }
////        });
////    }
//
//    private void sendLastMessage(String chattingId, String message, String messageKey, String date) {
//        HashMap<String, Object> valueMap = new HashMap<>();
//        valueMap.put("last_message", message);
//        valueMap.put("last_message_key", messageKey);
//        valueMap.put("last_message_delete", new ArrayList<String>());
//        valueMap.put("mdate", date);
//        getChatInfoDocuRef().document(chattingId).update(valueMap);
//    }
//
//    public void removeChattingEventListener() {
//        currentChattingRoomInfo = null;
//        currentChattingId = "";
//
//        onChattingMessageReceiveEventListener = null;
//        if (chattingMessageShotEventListener != null) {
//            chattingMessageShotEventListener.remove();
//            chattingMessageShotEventListener = null;
//        }
//
//        if (chattingMemberChangeEventListener != null) {
//            chattingMemberChangeEventListener.remove();
//            chattingMemberChangeEventListener = null;
//        }
//
//        loadPrevQuery = null;
//    }
//
//    private CollectionReference getMessageCollectionReference(String chattingId) {
//        return FireBaseHelper.getSingleInstance().getDatabase()
//                .collection(TB_CHAT_MESSAGE)
//                .document(chattingId)
//                .collection(TEMP_COLLECTION_NAME);
//    }
//
//    private Query getMessageQuery() {
//        return getMessageCollectionReference(currentChattingId).orderBy("key", Query.Direction.DESCENDING);
//    }
//
//    private boolean isAvailableMessage(ChatItem item) {
//        if (TextUtils.isEmpty(item.key)) {
//            return false;
//        }
//
//        long itemMessageTime = Long.parseLong(item.key);
//        long chattingRoomJoinTime = getChattingRoomJoinTime();
//        if (chattingRoomJoinTime > 0 && itemMessageTime <= chattingRoomJoinTime) {
//            DebugLogger.e("test", "getPrevMessage 참여전 메세지");
//            return false;
//        } else if (item.isDeletedMessage()) {
//            return false;
//        }
//
//        return true;
//    }
//
//    private Query loadPrevQuery = null;
//
//    public void getPrevMessage(final String firstMessageKey, boolean needLimit, final IGetMessageListListener listener) {
//        DebugLogger.i(TAG, "getPrevMessage : " + firstMessageKey + ", " + ", needLimit : " + needLimit);
//
//        Query query;
//
//        if (TextUtils.isEmpty(firstMessageKey) == false) {
//            if (loadPrevQuery == null) {
//                if (listener != null) {
//                    listener.onMessageListLoaded(null);
//                }
//                return;
//            }
//            DebugLogger.i(TAG, "getPrevMessage loadPrevQuery");
//            query = loadPrevQuery;
//        } else {
//            query = getMessageQuery();
//        }
//
//        if (needLimit) {
//            query = query.limit(MESSAGE_GET_COUNT);
//        }
//
//        final String chattingId = currentChattingId;
//
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                DebugLogger.i(TAG, "getPrevMessage onComplete");
//                if (TextUtils.isEmpty(currentChattingId) || chattingId.equals(currentChattingId) == false) {
//                    DebugLogger.e(TAG, "getPrevMessage is Not valid Data currentChattingId : " + currentChattingId + ", requestChattingId : " +chattingId);
//                    return;
//                }
//
//                ArrayList<ChattingItem> itemList = new ArrayList<ChattingItem>();
//                DocumentSnapshot firstDocumentSnapShot = null;
//                if (task.isSuccessful()) {
//                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
//                    Collections.reverse(docList);
//
//                    for (int i = 0; i < docList.size(); i++) {
//                        DocumentSnapshot document = docList.get(i);
//                        if (i == 0) {
//                            firstDocumentSnapShot = document;
//                        }
//
//                        String msgKey = document.getId();
//                        if (TextUtils.isEmpty(firstMessageKey) == false && firstMessageKey.equals(msgKey)) {
//                            DebugLogger.e("test", "onDataChange same key value");
//                            // 지정한 키값의 데이터도 같이 넘어오므로 같은 키값은 패스 한다.
//                            continue;
//                        }
//
//                        ChatItem item = document.toObject(ChatItem.class);
//                        if (isAvailableMessage(item) == false) {
//                            continue;
//                        }
//
//                        ChattingItem chattingItem = new ChattingItem(msgKey, item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
//                        itemList.add(chattingItem);
//                    }
//                }
//                DebugLogger.i("test", "getPrevMessage itemCount : " + itemList.size());
//                if (listener != null) {
//                    listener.onMessageListLoaded(itemList);
//                }
//
//                if (firstDocumentSnapShot != null) {
//                    loadPrevQuery = getMessageQuery().startAfter(firstDocumentSnapShot);
//                } else {
//                    loadPrevQuery = null;
//                }
//            }
//        });
//    }
//
//    private long getChattingRoomJoinTime() {
//        long chattingRoomJoinTime = 0;
//        if (currentChattingRoomInfo == null) {
//            currentChattingRoomInfo = ChattingHelper.getSingleInstance().getChattingRoomInfo(currentChattingId);
//        }
//
//        if (currentChattingRoomInfo != null) {
//            chattingRoomJoinTime = currentChattingRoomInfo.getJoinTime();
//        }
//
//        return chattingRoomJoinTime;
//    }
//
//    public void addChattingEventListener(final ChattingItem lastMessageItem, IChattingItemEventListener listener) {
//        // 중복 호출됐을경우 이벤트리스너를 처리하지 않기 위해 처리
//        if (onChattingMessageReceiveEventListener != null) {
//            return;
//        }
//
//        onChattingMessageReceiveEventListener = listener;
//
//        final long lastMessageTime = lastMessageItem != null ? Long.parseLong(lastMessageItem.getTimeStampKey()) : 0;
//
//        final String chattingId = currentChattingId;
//        chattingMessageShotEventListener = getMessageCollectionReference(chattingId).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        if (queryDocumentSnapshots != null) {
//                            DebugLogger.i("test", "chattingItem onSnapshots " + queryDocumentSnapshots.getDocumentChanges().size());
//
//                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                                QueryDocumentSnapshot eventItem = dc.getDocument();
//                                DebugLogger.i("test", "chattingItem onSnapshots type : " + dc.getType() + " , data : " + eventItem.getData());
//                                ChatItem item = eventItem.toObject(ChatItem.class);
//                                if (isAvailableMessage(item) == false) {
//                                    continue;
//                                }
//
//                                switch (dc.getType()) {
//                                    case ADDED:
//                                        if (onChattingMessageReceiveEventListener != null) {
//
//                                            long itemMessageTime = Long.parseLong(item.key);
//                                            if (itemMessageTime < lastMessageTime) {
//                                                continue;
//                                            } else if (itemMessageTime == lastMessageTime) {
//                                                if (lastMessageItem != null && lastMessageItem.getChatItem().msg.equals(item.msg)) {
//                                                    continue;
//                                                }
//                                            }
//
//                                            ChattingItem chattingItem = new ChattingItem(eventItem.getId(), item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
//                                            onChattingMessageReceiveEventListener.onItemAdded(chattingItem);
//                                        }
//                                        break;
//                                    case MODIFIED:
//                                        if (onChattingMessageReceiveEventListener != null) {
//                                            ChattingItem chattingItem = new ChattingItem(eventItem.getId(), item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
//                                            onChattingMessageReceiveEventListener.onItemChanged(chattingItem);
//                                        }
//                                        break;
//                                    case REMOVED:
//                                        break;
//                                }
//                            }
//                        }
//                    }
//                });
//    }
//
//    public void sendChattingMessageDeletedEvent(ChattingItem item) {
//        if (onChattingMessageReceiveEventListener != null) {
//            onChattingMessageReceiveEventListener.onItemRemoved(item);
//        }
//    }
//
//    public void updateMessageRead(ArrayList<ChattingItem> itemList) {
//        if (itemList == null || itemList.size() <= 0) {
//            return;
//        }
//
//        DebugLogger.i("test", "updateMessageRead itemList " + itemList.size());
//        FirebaseFirestore db = FireBaseHelper.getSingleInstance().getDatabase();
//        WriteBatch batch = null;
//        String loginUserId = LoginHelper.getSingleInstance().getLoginUserId();
//
//        for (ChattingItem item : itemList) {
//            ArrayList<String> notRead = item.getChatItem().not_read;
//            if (notRead != null && notRead.contains(loginUserId)) {
//                notRead.remove(loginUserId);
//                DocumentReference chattingItemDocu = getMessageCollectionReference(currentChattingId).document(item.getMsgKey());
//
//                if (batch == null) {
//                    batch = db.batch();
//                }
//
//                batch.update(chattingItemDocu, "not_read", notRead);
//            }
//        }
//
//        if (batch != null) {
//            batch.commit();
//        }
//    }
//
//    private void removeChattingRoomDetailInfoListener(String chatId) {
//        DebugLogger.i("removeRegisteredChattingRoomDetailInfo : " + chattingRoomDetailEventListener.size());
//        if (chattingRoomDetailEventListener.size() == 0) {
//            return;
//        }
//
//        synchronized (chattingRoomDetailEventListener) {
//            try {
//                ListenerRegistration listener = chattingRoomDetailEventListener.get(chatId);
//                listener.remove();
//                chattingRoomDetailEventListener.remove(chatId);
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void removeRegisteredChattingRoomDetailInfo() {
//        DebugLogger.i("removeRegisteredChattingRoomDetailInfo : " + chattingRoomDetailEventListener.size());
//        if (chattingRoomDetailEventListener.size() == 0) {
//            return;
//        }
//
//        synchronized (chattingRoomDetailEventListener) {
//            try {
//                for (String key : chattingRoomDetailEventListener.keySet()) {
//                    DebugLogger.i("removeRegisteredChattingRoomDetailInfo refPath : " + key);
//                    ListenerRegistration listener = chattingRoomDetailEventListener.get(key);
//                    listener.remove();
//                }
//                chattingRoomDetailEventListener.clear();
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void removeRegisteredChattingMemberEventListener(String chatId) {
//        DebugLogger.i("removeRegisteredChattingMemberEventListener : " + chattingRoomMemberEventListener.size());
//        if (chattingRoomMemberEventListener.size() == 0) {
//            return;
//        }
//
//        synchronized (chattingRoomMemberEventListener) {
//            try {
//                ListenerRegistration listener = chattingRoomMemberEventListener.get(chatId);
//                listener.remove();
//                chattingRoomMemberEventListener.remove(chatId);
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void removeRegisteredChattingMemberEventListener() {
//        DebugLogger.i("removeRegisteredChattingMemberEventListener : " + chattingRoomMemberEventListener.size());
//        if (chattingRoomMemberEventListener.size() == 0) {
//            return;
//        }
//
//        synchronized (chattingRoomMemberEventListener) {
//            try {
//                for (String key : chattingRoomMemberEventListener.keySet()) {
//                    DebugLogger.i("removeRegisteredChattingMemberEventListener refPath : " + key);
//                    ListenerRegistration listener = chattingRoomMemberEventListener.get(key);
//                    listener.remove();
//                }
//                chattingRoomMemberEventListener.clear();
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    // 나중에 사용할 수 도 있음
////    public void inviteMember(String chatId, ArrayList<CoverStarUser> selectedUserList, RetroCallback callback) {
////        if (selectedUserList == null || selectedUserList.size() <= 0) {
////            if (callback != null) {
////                callback.onFailure(null);
////            }
////            return;
////        }
////
////        StringBuilder sb = new StringBuilder();
////        for (CoverStarUser user : selectedUserList) {
////            sb.append(user.userId);
////            sb.append(",");
////        }
////
////        HashMap<String, String> bodyProperty = new HashMap<String, String>();
////        bodyProperty.put("chat_id", chatId);
////        bodyProperty.put("users", sb.toString().substring(0, sb.length() - 1));
////
////        RetroClient.getApiInterface().addMemberToChat(bodyProperty).enqueue(callback);
////    }
//
////    public void sendMemberChangeMessage(String chatId, String message, String messageType) {
////        HashMap<String, Object> valueMap = new HashMap<>();
////        valueMap.put("cdate", Util.getCurrentTimeToCommonFormat());
////        valueMap.put("key", "" + System.currentTimeMillis());
////        valueMap.put("msg", message);
////        valueMap.put("msg_type", "" + messageType);
////        valueMap.put("user_id", "" + LoginHelper.getSingleInstance().getLoginUserId());
////        valueMap.put("user_name", "");
////        valueMap.put("user_photo", "");
////        getMessageCollectionReference(chatId)
////                .add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
////            @Override
////            public void onComplete(@NonNull Task<DocumentReference> task) {
////                DebugLogger.i("test", "sendMemberChangeMessage complete");
////            }
////        });
////    }
//
////    public void deleteChattingRoom(final String chattingId, String deleteUser) {
////        HashMap<String, String> bodyProperty = new HashMap<String, String>();
////        bodyProperty.put("users", deleteUser);
////        bodyProperty.put("chat_id", chattingId);
////
////        RetroClient.getApiInterface().deleteChattingRoom(bodyProperty).enqueue(new RetroCallback<BaseResponse>() {
////            @Override
////            public void onSuccess(BaseResponse receivedData) {
////                String message = String.format(MyApplication.getContext().getString(R.string.leave_message), LoginHelper.getSingleInstance().getLoginUser().getUserName());
////                sendMemberChangeMessage(chattingId, message,ChattingHelper.MSG_TYPE_MEMBER_LEAVE);
////            }
////
////            @Override
////            public void onFailure(BaseResponse response) {
////            }
////        });
////    }
//
////    public void deleteChattingMessage(String chattingId, ChattingItem item) {
////        DebugLogger.i("test", "deleteChattingMessage");
////        String loginUserId = LoginHelper.getSingleInstance().getLoginUserId();
////
////        ArrayList<String> isDelete = item.getChatItem().is_delete;
////        if (isDelete == null) {
////            isDelete = new ArrayList<>();
////        }
////        isDelete.add(loginUserId);
////
////        getMessageCollectionReference(chattingId).document(item.getMsgKey()).update("is_delete", isDelete);
////
////        updateLastMessageDeleteText(chattingId, item.getTimeStampKey(), false);
////    }
//
////    public void deleteAllChattingMessage(final String chattingId) {
////        String selection = String.format(ChattingDBConstants.CHATTINGID_SELECTION, chattingId);
////        dbHelper.deleteQuery(ChattingDBConstants.MESSAGE_QUEUE_TABLE_NAME, selection);
////
////        DebugLogger.i("test", "deleteAllChattingMessage");
////        getMessageCollectionReference(chattingId)
////                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if (task.isSuccessful() == false) {
////                    return;
////                }
////
////                String loginUserId = LoginHelper.getSingleInstance().getLoginUserId();
////                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
////                    String msgKey = doc.getId();
////                    ChatItem item = doc.toObject(ChatItem.class);
////
////                    if (item.is_delete == null || item.is_delete.size() <= 0) {
////                        item.is_delete = new ArrayList<>();
////                    }
////                    ChattingItem chattingItem = new ChattingItem(msgKey, item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
////                    if (item.is_delete.contains(loginUserId) == false) {
////                        deleteChattingMessage(chattingId, chattingItem);
////                    }
////                }
////
////                updateLastMessageDeleteText(chattingId, "", true);
////            }
////        });
////    }
//
////    public void getImageMessageList(String chatId, final IGetMessageListListener listener) {
////        DebugLogger.i("test", "getImageMessageList");
////        getMessageQuery().whereEqualTo("msg_type", MSG_TYPE_IMAGE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                ArrayList<ChattingItem> itemList = new ArrayList<ChattingItem>();
////                if (task.isSuccessful()) {
////                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
////                    Collections.reverse(docList);
////
////                    for (DocumentSnapshot document : docList) {
////                        ChatItem item = document.toObject(ChatItem.class);
////                        if (isAvailableMessage(item) == false) {
////                            continue;
////                        }
////
////                        ChattingItem chattingItem = new ChattingItem(document.getId(), item, chatId, ChattingItem.SENDSTATE.SUCCESS);
////                        itemList.add(chattingItem);
////                    }
////                }
////                DebugLogger.i("test", "getImageMessageList itemCount : " + itemList.size());
////                if (listener != null) {
////                    listener.onMessageListLoaded(itemList);
////                }
////            }
////        });
////    }
}
