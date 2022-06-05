package com.shinleeholdings.coverstar.chatting;

import com.google.firebase.firestore.Exclude;
import com.shinleeholdings.coverstar.util.LoginHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatItem {
    public String key;  // 서버에 보낸 메세지가 파이어베이스 콜백으로 돌아올때 구분 키값
    public String cdate;
    public String msg;
    public String msg_type;
    public ArrayList<String> not_read;
    public ArrayList<String> is_delete;
    public String user_id;
    public String user_name;
    public String user_photo;
    public String fileSize;
    public String expireDate;
    public String width;
    public String height;
    public String filePath;
    public String filename;
    public String uploadType;

    public static final String KEY = "key";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String SENDUID = "send_uid";
    public static final String USER_NAME = "userName";
    public static final String USER_PHOTO = "userPhoto";
    public static final String FILESIZE = "fileSize";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String FILE_PATH = "filePath";
    public static final String UPLOADTYPE = "uploadType";

    public ChatItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatItem.class)
    }

    public ChatItem(String itemKey, String message, String msgType, ArrayList<String> notReadMember, String sendUid, String userName, String userPhoto, String fileSize, String expireDate, String width, String height) {
        key = itemKey;
        msg = message;
        msg_type = msgType;
        not_read = notReadMember;
        user_id = sendUid;
        user_name = userName;
        user_photo = userPhoto;
        this.fileSize = fileSize;
        this.expireDate = expireDate;
        this.width = width;
        this.height = height;
    }

    public ChatItem(JSONObject chatData) throws Exception {
        key = chatData.getString(KEY);
        msg = chatData.getString(MESSAGE);
        msg_type = chatData.getString(MESSAGE_TYPE);
        not_read = null;
        user_id = chatData.getString(SENDUID);
        user_name = chatData.getString(USER_NAME);
        user_photo = chatData.getString(USER_PHOTO);
        fileSize = chatData.getString(FILESIZE);
        expireDate = "";
        width = chatData.getString(WIDTH);
        height = chatData.getString(HEIGHT);
        filePath = chatData.getString(FILE_PATH);
        uploadType = chatData.getString(UPLOADTYPE);
    }

    @Exclude
    public String getJsonStringData() {
        String data = "";
        try {
            JSONObject json = new JSONObject();
            json.put(KEY, key);
            json.put(MESSAGE, msg);
            json.put(MESSAGE_TYPE, msg_type);
            json.put(SENDUID, user_id);
            json.put(USER_NAME, user_name);
            json.put(USER_PHOTO, user_photo);
            json.put(FILESIZE, fileSize);
            json.put(WIDTH, width);
            json.put(HEIGHT, height);
            json.put(FILE_PATH, filePath);
            json.put(UPLOADTYPE, uploadType);
            data = json.toString();
        } catch (Exception e) {
        }

        return data;
    }

    public boolean isDeletedMessage() {
        if (is_delete == null || is_delete.size() <= 0) {
            return false;
        }

        String loginUserSeq = LoginHelper.getSingleInstance().getLoginUserId();
        for (String userSeq : is_delete) {
            if (userSeq.equals(loginUserSeq)) {
                return true;
            }
        }
        return false;
    }
}
