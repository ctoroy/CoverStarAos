package com.shinleeholdings.coverstar.chatting;

public class ChattingRoomHelper {
    private static final String TAG = "ChattingRoomHelper";
    private static volatile ChattingRoomHelper instance;
    private final static Object lockObject = new Object();

    // 채팅방 정보
    // CHATROOM : collection
    //      ROOMID(roomInfo autoId) : Document
    //          MESSAGES : collection
    //              messageID(auto id) : document
    //                      메세지 정보
    private static final String FIRESTORE_TB_CHATROOM = "CHATROOM";
    private static final String FIRESTORE_COLLECTION_MESSAGES = "messages";

    private static final String FIELDNAME_SENDUSERID = "sendUserId";
    private static final String FIELDNAME_USERIMAGEPATH = "userImagePath";
    private static final String FIELDNAME_USERNICKNAME = "userNickName";
    private static final String FIELDNAME_MESSAGE = "message";
    private static final String FIELDNAME_MESSAGE_TIME = "messageTime";

    public static ChattingRoomHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new ChattingRoomHelper();
                }
            }
        }

        return instance;
    }
}
