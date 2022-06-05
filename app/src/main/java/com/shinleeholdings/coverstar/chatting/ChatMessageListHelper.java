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
import com.google.firebase.firestore.WriteBatch;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.FireBaseHelper;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import network.model.BaseResponse;
import network.model.DefaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class ChatMessageListHelper {
    private static final String TAG = "ChatMessageListHelper";
    private static volatile ChatMessageListHelper instance;
    private final static Object lockObject = new Object();

    private final DataBaseHelper dbHelper;

    public static final int MESSAGE_GET_COUNT = 100;

    public interface IChattingItemEventListener {
        void onItemAdded(ChattingItem item);

        void onItemChanged(ChattingItem item);

        void onItemRemoved(ChattingItem item);

        void onError(String message);
    }

    public interface IGetMessageListListener {
        void onMessageListLoaded(ArrayList<ChattingItem> itemList);
    }

    private String currentChattingId = "";
    private ChatRoomItem currentChatRoomItem;

    private IChattingItemEventListener onChattingMessageReceiveEventListener;
    private ListenerRegistration chattingMessageShotEventListener;

    public static ChatMessageListHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new ChatMessageListHelper();
                }
            }
        }

        return instance;
    }

    private ChatMessageListHelper() {
        dbHelper = DataBaseHelper.getSingleton(MyApplication.getContext());
        dbHelper.createTable(ChattingConstants.CREATE_TABLE_QUERY);
    }

    public ArrayList<ChattingItem> getSendFailMessageList() {
        ArrayList<ChattingItem> chattingList = new ArrayList<ChattingItem>();

        String selection = String.format(ChattingConstants.CHATTINGID_SELECTION, currentChattingId);
        Cursor cursor = dbHelper.query(ChattingConstants.MESSAGE_QUEUE_TABLE_NAME, null, selection, null, null, null, null);

        if (cursor == null) {
            return chattingList;
        }

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String data = cursor.getString(ChattingConstants.INDEX_DATA);
                if (TextUtils.isEmpty(data)) {
                    continue;
                }

                ChattingItem.SENDSTATE sendState = ChattingItem.SENDSTATE.FAIL;
                ChatItem chatItem;
                try {
                    chatItem = new ChatItem(new JSONObject(data));
                } catch (Exception e) {
                    DebugLogger.e("test", "loadChattingMessage error : " + e.getMessage());
                    continue;
                }

                ChattingItem item = new ChattingItem(chatItem, currentChattingId, sendState);
                chattingList.add(item);
            }
        }

        cursor.close();

        return chattingList;
    }

    public int saveChattingMessageToDataBase(ChattingItem item) {
        if (item == null) {
            return -1;
        }

        String itemKey = item.getTimeStampKey();
        String chattingId = item.getChattingId();
        String selection = String.format(ChattingConstants.SELECTION, chattingId, itemKey);
        String hasItemQuery = String.format(ChattingConstants.QUERY_HASITEM, chattingId, itemKey);

        ContentValues values = new ContentValues();
        values.put(ChattingConstants.FIELD_KEY, itemKey);
        values.put(ChattingConstants.FIELD_CHATTING_ID, chattingId);
        values.put(ChattingConstants.FIELD_DATA, item.getJsonStringData());

        return dbHelper.insertQuery(ChattingConstants.MESSAGE_QUEUE_TABLE_NAME, hasItemQuery, selection, values);
    }

    public int deleteChattingItem(ChattingItem item) {
        if (item == null) {
            return 0;
        }

        String selection = String.format(ChattingConstants.SELECTION, item.getChattingId(), item.getTimeStampKey());
        return dbHelper.deleteQuery(ChattingConstants.MESSAGE_QUEUE_TABLE_NAME, selection);
    }

    public void setCurrentChattingId(String chattingId) {
        currentChattingId = chattingId;
    }

    public String getCurrentChattingId() {
        return currentChattingId;
    }

    public void sendChattingMessageToServer(final String chattingId, final ChattingItem item, final RetroCallback<BaseResponse> callbackListener) {
        ChatItem chatItem = item.getChatItem();
        HashMap<String, String> bodyProperty = new HashMap<String, String>();
        bodyProperty.put("chat_id", chattingId);
        bodyProperty.put("msg", chatItem.msg);
        bodyProperty.put("msg_type", chatItem.msg_type);
        bodyProperty.put("user_id", LoginHelper.getSingleInstance().getLoginUserId());
        bodyProperty.put("keyx", item.getTimeStampKey() + "");

        StringBuilder sb = new StringBuilder();
        for (String userId : item.getChatItem().not_read) {
            sb.append(userId);
            sb.append(",");
        }
        bodyProperty.put("users", sb.toString().substring(0, sb.length() - 1));

        RetroClient.getApiInterface().sendChattingMessage(bodyProperty).enqueue(new RetroCallback<DefaultResult>() {
            @Override
            public void onSuccess(BaseResponse receivedData) {
                sendLastMessage(chattingId, chatItem.msg, item.getTimeStampKey() + "", Util.getCurrentTimeToCommonFormat());
                if (callbackListener != null) {
                    callbackListener.onSuccess(receivedData);
                }
            }

            @Override
            public void onFailure(BaseResponse response) {
                if (callbackListener != null) {
                    callbackListener.onFailure(response);
                }
            }
        });
    }

    private void sendLastMessage(String chattingId, String message, String messageKey, String date) {
        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put("last_message", message);
        valueMap.put("last_message_key", messageKey);
        valueMap.put("last_message_delete", new ArrayList<String>());
        valueMap.put("mdate", date);
        ChatRoomListHelper.getSingleInstance().getChatInfoDocuRef().document(chattingId).update(valueMap);
    }

    public void removeChattingEventListener() {
        currentChatRoomItem = null;
        currentChattingId = "";

        onChattingMessageReceiveEventListener = null;
        if (chattingMessageShotEventListener != null) {
            chattingMessageShotEventListener.remove();
            chattingMessageShotEventListener = null;
        }

        prevChatMessageLoadQuery = null;
    }

    private CollectionReference getMessageCollectionReference(String chattingId) {
        return FireBaseHelper.getSingleInstance().getDatabase()
                .collection(ChattingConstants.TB_CHAT_MESSAGE)
                .document(chattingId)
                .collection(ChattingConstants.TEMP_COLLECTION_NAME);
    }

    private Query getMessageQuery() {
        return getMessageCollectionReference(currentChattingId).orderBy("key", Query.Direction.DESCENDING);
    }

    private Query prevChatMessageLoadQuery = null;

    public void getPrevMessage(final String firstMessageKey, boolean needLimit, final IGetMessageListListener listener) {
        DebugLogger.i(TAG, "getPrevMessage : " + firstMessageKey + ", " + ", needLimit : " + needLimit);

        Query query;

        if (TextUtils.isEmpty(firstMessageKey) == false) {
            if (prevChatMessageLoadQuery == null) {
                if (listener != null) {
                    listener.onMessageListLoaded(null);
                }
                return;
            }
            DebugLogger.i(TAG, "getPrevMessage loadPrevQuery");
            query = prevChatMessageLoadQuery;
        } else {
            query = getMessageQuery();
        }

        if (needLimit) {
            query = query.limit(MESSAGE_GET_COUNT);
        }

        final String chattingId = currentChattingId;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DebugLogger.i(TAG, "getPrevMessage onComplete");
                if (TextUtils.isEmpty(currentChattingId) || chattingId.equals(currentChattingId) == false) {
                    DebugLogger.e(TAG, "getPrevMessage is Not valid Data currentChattingId : " + currentChattingId + ", requestChattingId : " +chattingId);
                    return;
                }

                ArrayList<ChattingItem> itemList = new ArrayList<ChattingItem>();
                DocumentSnapshot firstDocumentSnapShot = null;
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
                    Collections.reverse(docList);

                    for (int i = 0; i < docList.size(); i++) {
                        DocumentSnapshot document = docList.get(i);
                        if (i == 0) {
                            firstDocumentSnapShot = document;
                        }

                        String msgKey = document.getId();
                        if (TextUtils.isEmpty(firstMessageKey) == false && firstMessageKey.equals(msgKey)) {
                            DebugLogger.e("test", "onDataChange same key value");
                            // 지정한 키값의 데이터도 같이 넘어오므로 같은 키값은 패스 한다.
                            continue;
                        }

                        ChatItem item = document.toObject(ChatItem.class);

                        ChattingItem chattingItem = new ChattingItem(msgKey, item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
                        itemList.add(chattingItem);
                    }
                }
                DebugLogger.i("test", "getPrevMessage itemCount : " + itemList.size());
                if (listener != null) {
                    listener.onMessageListLoaded(itemList);
                }

                if (firstDocumentSnapShot != null) {
                    prevChatMessageLoadQuery = getMessageQuery().startAfter(firstDocumentSnapShot);
                } else {
                    prevChatMessageLoadQuery = null;
                }
            }
        });
    }

    public void addChattingEventListener(final ChattingItem lastMessageItem, IChattingItemEventListener listener) {
        // 중복 호출됐을경우 이벤트리스너를 처리하지 않기 위해 처리
        if (onChattingMessageReceiveEventListener != null) {
            return;
        }

        onChattingMessageReceiveEventListener = listener;

        final long lastMessageTime = lastMessageItem != null ? Long.parseLong(lastMessageItem.getTimeStampKey()) : 0;

        final String chattingId = currentChattingId;
        chattingMessageShotEventListener = getMessageCollectionReference(chattingId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            DebugLogger.i("test", "chattingItem onSnapshots " + queryDocumentSnapshots.getDocumentChanges().size());

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                QueryDocumentSnapshot eventItem = dc.getDocument();
                                DebugLogger.i("test", "chattingItem onSnapshots type : " + dc.getType() + " , data : " + eventItem.getData());
                                ChatItem item = eventItem.toObject(ChatItem.class);

                                switch (dc.getType()) {
                                    case ADDED:
                                        if (onChattingMessageReceiveEventListener != null) {

                                            long itemMessageTime = Long.parseLong(item.key);
                                            if (itemMessageTime < lastMessageTime) {
                                                continue;
                                            } else if (itemMessageTime == lastMessageTime) {
                                                if (lastMessageItem != null && lastMessageItem.getChatItem().msg.equals(item.msg)) {
                                                    continue;
                                                }
                                            }

                                            ChattingItem chattingItem = new ChattingItem(eventItem.getId(), item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
                                            onChattingMessageReceiveEventListener.onItemAdded(chattingItem);
                                        }
                                        break;
                                    case MODIFIED:
                                        if (onChattingMessageReceiveEventListener != null) {
                                            ChattingItem chattingItem = new ChattingItem(eventItem.getId(), item, chattingId, ChattingItem.SENDSTATE.SUCCESS);
                                            onChattingMessageReceiveEventListener.onItemChanged(chattingItem);
                                        }
                                        break;
                                    case REMOVED:
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    public void updateMessageRead(ArrayList<ChattingItem> itemList) {
        if (itemList == null || itemList.size() <= 0) {
            return;
        }

        DebugLogger.i("test", "updateMessageRead itemList " + itemList.size());
        FirebaseFirestore db = FireBaseHelper.getSingleInstance().getDatabase();
        WriteBatch batch = null;
        String loginUserId = LoginHelper.getSingleInstance().getLoginUserId();

        for (ChattingItem item : itemList) {
            ArrayList<String> notRead = item.getChatItem().not_read;
            if (notRead != null && notRead.contains(loginUserId)) {
                notRead.remove(loginUserId);
                DocumentReference chattingItemDocu = getMessageCollectionReference(currentChattingId).document(item.getMsgKey());

                if (batch == null) {
                    batch = db.batch();
                }

                batch.update(chattingItemDocu, "not_read", notRead);
            }
        }

        if (batch != null) {
            batch.commit();
        }
    }
}
