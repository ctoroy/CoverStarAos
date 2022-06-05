package com.shinleeholdings.coverstar.chatting;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.FireBaseHelper;
import com.shinleeholdings.coverstar.util.LoginHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ChatRoomListHelper {
    private static final String TAG = "ChatRoomListHelper";
    private static volatile ChatRoomListHelper instance;
    private final static Object lockObject = new Object();

    private ListenerRegistration chatRoomListEventListener;
    private ArrayList<ChatRoomItem> chattingRoomList = null;

    private HashMap<String, ArrayList<ChattingRoomMember>> chattingRoomMemberHashMap = new HashMap<>();

    private final ArrayList<IChattingRoomEventListener> chattingRoomInfoUpdateListeners = new ArrayList<>();

    private final HashMap<String, ListenerRegistration> chattingRoomDetailEventListener = new HashMap<>();
    private final HashMap<String, ListenerRegistration> chattingRoomMemberEventListener = new HashMap<>();

    private boolean chattingListLoadingCompleted = false;

    public interface IChattingRoomEventListener {
        void onChattingRoomLoadCompleted();

        void onChattingRoomDetailInfoChange(String chatId, ChatRoomItem item);

        void onChattingRoomAdded(String chatId, ChatRoomItem item);

        void onChattingRoomInfoChanged(String chatId);

        void onChattingRoomRemoved(String chatId);

        void onMemberUpdate(String chatId);
    }

    public static ChatRoomListHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new ChatRoomListHelper();
                }
            }
        }

        return instance;
    }

    private ChatRoomListHelper() {
    }

    public void addChattingRoomListListener(IChattingRoomEventListener chattingRoomListListener) {
        synchronized (chattingRoomInfoUpdateListeners) {
            if (chattingRoomInfoUpdateListeners.contains(chattingRoomListListener)) {
                return;
            }
            chattingRoomInfoUpdateListeners.add(chattingRoomListListener);
        }
    }

    public void removeChattingRoomListListener(IChattingRoomEventListener chattingRoomListListener) {
        synchronized (chattingRoomInfoUpdateListeners) {
            if (chattingRoomInfoUpdateListeners.size() == 0) {
                return;
            }
            chattingRoomInfoUpdateListeners.remove(chattingRoomListListener);
        }
    }

    private void sendChattingRoomAddedEvent(final String chatId, final ChatRoomItem item) {
        synchronized (chattingRoomInfoUpdateListeners) {
            try {
                if (chattingRoomInfoUpdateListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
                                if (listener != null) {
                                    listener.onChattingRoomAdded(chatId, item);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void sendChattingRoomListLoadCompleteEvent() {
        synchronized (chattingRoomInfoUpdateListeners) {
            try {
                if (chattingRoomInfoUpdateListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
                                if (listener != null) {
                                    listener.onChattingRoomLoadCompleted();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void sendChattingRoomDetailInfoEvent(final String chatId, final ChatRoomItem item) {
        synchronized (chattingRoomInfoUpdateListeners) {
            try {
                if (chattingRoomInfoUpdateListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
                                if (listener != null) {
                                    listener.onChattingRoomDetailInfoChange(chatId, item);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void sendChattingRoomInfoChangeEvent(final String chatId) {
        synchronized (chattingRoomInfoUpdateListeners) {
            try {
                if (chattingRoomInfoUpdateListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
                                if (listener != null) {
                                    listener.onChattingRoomInfoChanged(chatId);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void sendChattingMemberUpdateEvent(final String chatId) {
        synchronized (chattingRoomInfoUpdateListeners) {
            try {
                if (chattingRoomInfoUpdateListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
                                if (listener != null) {
                                    listener.onMemberUpdate(chatId);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void sendChattingRoomRemoveEvent(final String chatId) {
        synchronized (chattingRoomInfoUpdateListeners) {
            try {
                if (chattingRoomInfoUpdateListeners.size() == 0) {
                    return;
                }

                MyApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (IChattingRoomEventListener listener : chattingRoomInfoUpdateListeners) {
                                if (listener != null) {
                                    listener.onChattingRoomRemoved(chatId);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    public void initChattingListListener() {
        if (chatRoomListEventListener != null) {
            chatRoomListEventListener.remove();
        }
        removeRegisteredChattingRoomDetailInfo();
        removeRegisteredChattingMemberEventListener();
    }

    public ArrayList<ChatRoomItem> getChattingRoomList() {
        return chattingRoomList;
    }

    public boolean isChattingListLoadingCompleted() {
        return chattingListLoadingCompleted;
    }

    private CollectionReference getChatListCollectionRef() {
        return FireBaseHelper.getSingleInstance().getDatabase()
                .collection(ChattingConstants.TB_CHAT_LIST)
                .document(LoginHelper.getSingleInstance().getLoginUserId())
                .collection(ChattingConstants.TEMP_COLLECTION_NAME);
    }

    public ChatRoomItem getChatRoomItem(String chatId) {
        if (TextUtils.isEmpty(chatId) || chattingRoomList == null || chattingRoomList.size() <= 0) {
            return null;
        }

        for (ChatRoomItem item : chattingRoomList) {
            if (item.getChatId().equals(chatId)) {
                return item;
            }
        }

        return null;
    }

    private void addChatRoomItem(String chatId, Map<String, Object> data, boolean isAdded) {
        DebugLogger.e("test", "TB_CHAT_LIST addChattingRoomItem chatId : " + chatId + " , data : " + data);
        long badgeCount = (Long) data.get(ChattingConstants.FIELDNAME_BADGE_CNT);
        String customName = (String) data.get(ChattingConstants.FIELDNAME_CUSTOM_ROOM_NAME);

        ChatRoomItem item = new ChatRoomItem();
        item.setChatId(chatId);
        item.setCustomRoomName(customName);

        BadgeManager.getSingleInstance().setBadgeInfo(chatId, badgeCount);
        chattingRoomList.add(item);

        if (isAdded) {
            sendChattingRoomAddedEvent(chatId,item);
        }

        getChattingRoomDetailInfo(item);
    }

    public void getChatRoomListInfo() {
        BadgeManager.getSingleInstance().initBadgeInfo();
        removeRegisteredChattingRoomDetailInfo();
        removeRegisteredChattingMemberEventListener();
        chattingRoomList = null;
        chattingRoomMemberHashMap.clear();
        chattingListLoadingCompleted = false;
        if (chatRoomListEventListener != null) {
            chatRoomListEventListener.remove();
        }

        getChatListCollectionRef().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() == false) {
                    return;
                }

                if (chattingRoomList == null) {
                    chattingRoomList = new ArrayList<>();
                }

                DebugLogger.e("test", "TB_CHAT_LIST getChattingRoomListInfo success");
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    String chatId = doc.getId();
                    Map<String, Object> data = doc.getData();
                    addChatRoomItem(chatId, data, false);
                }
                chattingListLoadingCompleted = true;
                sendChattingRoomListLoadCompleteEvent();
                chatRoomListEventListener = getChatListCollectionRef().addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots == null) {
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            QueryDocumentSnapshot documentSnapshot = dc.getDocument();
                            String chatId = documentSnapshot.getId();

                            Map<String, Object> data = dc.getDocument().getData();
                            DebugLogger.e("test", "TB_CHAT_LIST chatId : " + chatId + ", Type : " + dc.getType() + " , data : " + data);

                            switch (dc.getType()) {
                                case ADDED:
                                    if (chattingRoomList == null) {
                                        chattingRoomList = new ArrayList<>();
                                    }

                                    for (ChatRoomItem chatRoomItem : chattingRoomList) {
                                        if (chatId.equals(chatRoomItem.getChatId())) {
                                            return;
                                        }
                                    }

                                    addChatRoomItem(chatId, data, true);
                                    break;
                                case MODIFIED:
                                    long badgeCount = (Long) data.get(ChattingConstants.FIELDNAME_BADGE_CNT);
                                    String customName = (String) data.get(ChattingConstants.FIELDNAME_CUSTOM_ROOM_NAME);

                                    for (ChatRoomItem chattingItem : chattingRoomList) {
                                        if (chattingItem.getChatId().equals(chatId)) {
                                            chattingItem.setCustomRoomName(customName);
                                            break;
                                        }
                                    }

                                    // TODO
//                                    if (chatId.equals(ChatRoomListHelper.getSingleInstance().getCurrentChattingId())) {
//                                        badgeCount = 0;
//                                        ChatRoomListHelper.getSingleInstance().updateChatListInfo(chatId, ChatRoomListHelper.FIELDNAME_BADGE_CNT, badgeCount);
//                                    }

                                    BadgeManager.getSingleInstance().updateBadgeInfo(chatId, badgeCount);
                                    sendChattingRoomInfoChangeEvent(chatId);
                                    break;
                                case REMOVED:
                                    for (ChatRoomItem chattingItem : chattingRoomList) {
                                        if (chattingItem.getChatId().equals(chatId)) {
                                            chattingRoomList.remove(chattingItem);
                                            break;
                                        }
                                    }
                                    removeChattingRoomDetailInfoListener(chatId);
                                    chattingRoomMemberHashMap.remove(chatId);
                                    removeRegisteredChattingMemberEventListener(chatId);
                                    BadgeManager.getSingleInstance().deleteBadgeInfo(chatId);
                                    sendChattingRoomRemoveEvent(chatId);

                                    break;
                            }
                        }
                    }
                });
            }
        });
    }

    public CollectionReference getChatInfoDocuRef() {
        return FireBaseHelper.getSingleInstance().getDatabase().collection(ChattingConstants.TB_CHAT_INFO);
    }

    private void getChattingRoomDetailInfo(final ChatRoomItem chattingItem) {
        final String chatId = chattingItem.getChatId();
        DebugLogger.e(TAG, "getChattingRoomDetailInfo chattingId : " + chatId);

        CollectionReference docuRef = getChatInfoDocuRef();
        getChattingRoomMemberInfo(chatId, docuRef);

        ListenerRegistration listener = docuRef.document(chatId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapShot, @Nullable FirebaseFirestoreException e) {
                DebugLogger.i(TAG, "getChattingRoomDetailInfo onComplete getResult chatId : " + chatId + ",  data : " + snapShot);
                chattingItem.setLastMessage((String) snapShot.get("last_message"));
                chattingItem.setMessageDate((String) snapShot.get("mdate"));
                chattingItem.setRoomName((String) snapShot.get("room_name"));

                try {
                    synchronized (instance) {
                        String lastMessageKey = (String) snapShot.get("last_message_key");
                        chattingItem.setLastMessageKey(lastMessageKey);

                        ArrayList<String> deleteInfoList = (ArrayList<String>) snapShot.get("last_message_delete");
                        chattingItem.setDeleteInfoList(deleteInfoList);

                        DebugLogger.i("test", "lastMessageTest getChattingRoomDetailInfo lastMessageKey : " + lastMessageKey);
                        DebugLogger.i("test", "lastMessageTest getChattingRoomDetailInfo deleteInfoList : " + deleteInfoList);

                        if (deleteInfoList != null && deleteInfoList.size() > 0) {
                            String loginUserSeq = LoginHelper.getSingleInstance().getLoginUserId();
                            for (String userSeq : deleteInfoList) {
                                if (userSeq.equals(loginUserSeq)) {
                                    DebugLogger.e("test", "lastMessageTest getChattingRoomDetailInfo deleteInfoList has mySeq");
                                    // 삭제한 사용자 목록에 내 아이디가 있을 경우
                                    chattingItem.setLastMessage("삭제한 메세지 입니다.");
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e1) {
                    DebugLogger.i("test", "lastMessageTest getChattingRoomDetailInfo lastMessageInfo Error : " + e.getMessage());
                }

                sendChattingRoomDetailInfoEvent(chatId,chattingItem);
            }
        });

        chattingRoomDetailEventListener.put(chatId, listener);
    }

    public ArrayList<ChattingRoomMember> getChattingRoomMember(String chatId) {
        return chattingRoomMemberHashMap.get(chatId);
    }

    private void getChattingRoomMemberInfo(final String chatId, CollectionReference docuRef) {
        DebugLogger.i(TAG, "getChattingRoomMemberInfo chatId : " + chatId);
        ListenerRegistration listener = docuRef.document(chatId)
                .collection("room_member")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        try {
                            ArrayList<ChattingRoomMember> hashedMemberList = getChattingRoomMember(chatId);
                            if (hashedMemberList == null) {
                                hashedMemberList = new ArrayList<>();
                                chattingRoomMemberHashMap.put(chatId, hashedMemberList);
                            }

                            if (queryDocumentSnapshots != null) {
                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    Map<String, Object> data = dc.getDocument().getData();
                                    DebugLogger.i(TAG, "getChattingRoomMemberInfo chatId : " + chatId + " type : " + dc.getType() + ", userName : " + data);
                                    String userId = (String) data.get(ChattingConstants.FIELDNAME_USER_ID);

                                    if (dc.getType() == DocumentChange.Type.ADDED) {

                                        boolean alreadyHasMember = false;
                                        for (ChattingRoomMember hasMember : hashedMemberList) {
                                            if (hasMember.getUserId().equals(userId)) {
                                                hasMember.setUserNickName((String) data.get(ChattingConstants.FIELDNAME_USER_NAME));
                                                hasMember.setUserPhoto((String) data.get(ChattingConstants.FIELDNAME_USER_PHOTO));
                                                alreadyHasMember = true;
                                                break;
                                            }
                                        }
                                        if (alreadyHasMember == false) {
                                            ChattingRoomMember member = new ChattingRoomMember();
                                            member.setUserId(userId);
                                            member.setUserNickName((String) data.get(ChattingConstants.FIELDNAME_USER_NAME));
                                            member.setUserPhoto((String) data.get(ChattingConstants.FIELDNAME_USER_PHOTO));
                                            hashedMemberList.add(member);
                                        }
                                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                        for (ChattingRoomMember member : hashedMemberList) {
                                            if (member.getUserId().equals(userId)) {
                                                hashedMemberList.remove(member);
                                                break;
                                            }
                                        }
                                    } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                        for (ChattingRoomMember member : hashedMemberList) {
                                            if (member.getUserId().equals(userId)) {
                                                member.setUserNickName((String) data.get(ChattingConstants.FIELDNAME_USER_NAME));
                                                member.setUserPhoto((String) data.get(ChattingConstants.FIELDNAME_USER_PHOTO));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            sendChattingMemberUpdateEvent(chatId);
                        } catch (Exception exception) {
                        }
                    }
                });

        chattingRoomMemberEventListener.put(chatId, listener);
    }

    // TODO 채팅방 시작
//    public void createChatRoom(final Activity activity, CoverStarUser userData, String friendsNames, final RetroCallback<ChatCreate> callback) {
//        if (userData == null) {
//            return;
//        }
//        ArrayList<CoverStarUser> selectedUserList = new ArrayList<>();
//        selectedUserList.add(userData);
//
//        String myUserId = LoginHelper.getSingleInstance().getLoginUserId();
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(myUserId);
//        sb.append(",");
//
//        for (int i = 0; i < selectedUserList.size(); i++) {
//            CoverStarUser user = selectedUserList.get(i);
//            sb.append(user.userId);
//
//            if (i < selectedUserList.size() - 1) {
//                sb.append(",");
//            }
//        }
//
//        createChatRoom(activity, sb.toString(), friendsNames, callback);
//    }

    // TODO 채팅방 시작
//    public void createChatRoom(final Activity activity, String userSeq, final String friendsNames, final RetroCallback<ChatCreate> callback) {
//        HashMap<String, String> bodyProperty = new HashMap<String, String>();
//        bodyProperty.put("users", userSeq);
//
//        DebugLogger.i("test", "startChat users : " + userSeq);
//
//        ProgressDialogHelper.show(activity);
//        RetroClient.getApiInterface().createChattingRoom(bodyProperty).enqueue(new RetroCallback<ChatCreate>() {
//            @Override
//            public void onSuccess(BaseResponse receivedData) {
//                ProgressDialogHelper.dismiss();
//                ChatCreate result = (ChatCreate) receivedData;
//                String chattingRoomId = result.getChattingRoomId();
//                startChatActivity(activity, chattingRoomId);
//
//                String resultMessage = receivedData.getMessage();
//                String alreadyMessage = "이미 있는 방입니다.";
//                if (alreadyMessage.equals(resultMessage) == false) {
//                    String message = String.format(activity.getString(R.string.invite_message), LoginHelper.getSingleInstance().getLoginUser().getUserName(), friendsNames);
//                    sendMemberChangeMessage(chattingRoomId, message, ChattingHelper.MSG_TYPE_MEMBER_ENTER);
//                }
//
//                if (callback != null) {
//                    callback.onSuccess(receivedData);
//                }
//            }
//
//            @Override
//            public void onFailure(BaseResponse response) {
//                ProgressDialogHelper.dismiss();
//                if (callback != null) {
//                    callback.onFailure(response);
//                }
//            }
//        });
//    }

    // TODO 채팅방 시작
//    public void startChatActivity(Activity activity, String chattingId) {
//        Intent intent = new Intent(activity, ChatActivity.class);
//        intent.putExtra(AppConstants.EXTRA.CHAT_ID, chattingId);
//        activity.startActivity(intent);
//    }

    public void updateLastMessageDeleteText(final String chattingId, String messageKey, boolean force) {
        DebugLogger.i("test", "lastMessageTest updateLastMessageDeleteText chattingId : " + chattingId + ", messageKey : " + messageKey);
        // 삭제하는 메세지와 chat_info의 채팅방 정보에 있는 last_message_key값을 비교해서 같은 키값이면 delete에 내 아이디 추가
        if (TextUtils.isEmpty(chattingId)) {
            return;
        }

        if (force == false && TextUtils.isEmpty(messageKey)) {
            return;
        }

        for (ChatRoomItem item : chattingRoomList) {
            if (item.getChatId().equals(chattingId) == false) {
                continue;
            }

            if (force || (TextUtils.isEmpty(item.getLastMessageKey()) == false && item.getLastMessageKey().equals(messageKey))) {
                final DocumentReference sfDocRef = getChatInfoDocuRef().document(chattingId);
                FireBaseHelper.getSingleInstance().getDatabase().runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapShot = transaction.get(sfDocRef);
                        try {
                            if (force == false) {
                                String lastMessageKey = (String) snapShot.get("last_message_key");
                                if (lastMessageKey.equals(messageKey) == false) {
                                    return null;
                                }
                            }

                            DebugLogger.i("test", "lastMessageTest updateLastMessageDeleteText add myId");
                            ArrayList<String> isDelete = (ArrayList<String>) snapShot.get("last_message_delete");
                            if (isDelete == null) {
                                isDelete = new ArrayList<>();
                            }
                            String loginUserSeq = LoginHelper.getSingleInstance().getLoginUserId();
                            if (isDelete.contains(loginUserSeq) == false) {
                                isDelete.add(LoginHelper.getSingleInstance().getLoginUserId());
                                sfDocRef.update("last_message_delete", isDelete);
                            }
                        } catch (Exception e1) {
                            DebugLogger.i("test", "lastMessageTest updateLastMessageDeleteText add myId error : " + e1.getMessage());
                        }
                        return null;
                    }
                });
                break;
            }
        }
    }

    private void removeChattingRoomDetailInfoListener(String chatId) {
        DebugLogger.i("removeRegisteredChattingRoomDetailInfo : " + chattingRoomDetailEventListener.size());
        if (chattingRoomDetailEventListener.size() == 0) {
            return;
        }

        synchronized (chattingRoomDetailEventListener) {
            try {
                ListenerRegistration listener = chattingRoomDetailEventListener.get(chatId);
                listener.remove();
                chattingRoomDetailEventListener.remove(chatId);
            } catch (Exception e) {
            }
        }
    }

    private void removeRegisteredChattingRoomDetailInfo() {
        DebugLogger.i("removeRegisteredChattingRoomDetailInfo : " + chattingRoomDetailEventListener.size());
        if (chattingRoomDetailEventListener.size() == 0) {
            return;
        }

        synchronized (chattingRoomDetailEventListener) {
            try {
                for (String key : chattingRoomDetailEventListener.keySet()) {
                    DebugLogger.i("removeRegisteredChattingRoomDetailInfo refPath : " + key);
                    ListenerRegistration listener = chattingRoomDetailEventListener.get(key);
                    listener.remove();
                }
                chattingRoomDetailEventListener.clear();
            } catch (Exception e) {
            }
        }
    }

    private void removeRegisteredChattingMemberEventListener(String chatId) {
        DebugLogger.i("removeRegisteredChattingMemberEventListener : " + chattingRoomMemberEventListener.size());
        if (chattingRoomMemberEventListener.size() == 0) {
            return;
        }

        synchronized (chattingRoomMemberEventListener) {
            try {
                ListenerRegistration listener = chattingRoomMemberEventListener.get(chatId);
                listener.remove();
                chattingRoomMemberEventListener.remove(chatId);
            } catch (Exception e) {
            }
        }
    }

    private void removeRegisteredChattingMemberEventListener() {
        DebugLogger.i("removeRegisteredChattingMemberEventListener : " + chattingRoomMemberEventListener.size());
        if (chattingRoomMemberEventListener.size() == 0) {
            return;
        }

        synchronized (chattingRoomMemberEventListener) {
            try {
                for (String key : chattingRoomMemberEventListener.keySet()) {
                    DebugLogger.i("removeRegisteredChattingMemberEventListener refPath : " + key);
                    ListenerRegistration listener = chattingRoomMemberEventListener.get(key);
                    listener.remove();
                }
                chattingRoomMemberEventListener.clear();
            } catch (Exception e) {
            }
        }
    }
}
