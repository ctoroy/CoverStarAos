package com.shinleeholdings.coverstar.chatting;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.data.ReplyItem;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.FireBaseHelper;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChattingHelper {
    private static final String TAG = "CommentHelper";
    private static volatile ChattingHelper instance;
    private final static Object lockObject = new Object();

    // TODO 채팅방 목록 구조 허브톡 참고해서 다시 확인 필요

    // 채팅방 목록
    // CHATLIST : collection
    //      ROOMINFO(auto id) : document
    //          lastMessage : String
    //          lastMessageTime : Long
    //          memberList : Array<String>
    public static final String FIRESTORE_TB_CHATTINGLIST = "CHATLIST";
    public static final String FIELDNAME_MEMBERS = "memberList";

    // 채팅방 정보
    // CHATROOM : collection
    //      ROOMID(roomInfo autoId) : Document
    //          MESSAGES : collection
    //              messageID(auto id) : document
    //                      메세지 정보
    public static final String FIRESTORE_TB_CHATROOM = "CHATROOM";
    public static final String FIRESTORE_COLLECTION_MESSAGES = "messages";

    public static final String FIELDNAME_SENDUSERID = "sendUserId";
    public static final String FIELDNAME_USERIMAGEPATH = "userImagePath";
    public static final String FIELDNAME_USERNICKNAME = "userNickName";
    public static final String FIELDNAME_MESSAGE = "message";
    public static final String FIELDNAME_MESSAGE_TIME = "messageTime";

    public static ChattingHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new ChattingHelper();
                }
            }
        }

        return instance;
    }

    public void getCommentListRef(String userId) {
        FireBaseHelper.getSingleInstance().getDatabase()
                .collection(FIRESTORE_TB_CHATTINGLIST).whereArrayContains(FIELDNAME_MEMBERS, userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });
    }
}
