package com.shinleeholdings.coverstar.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shinleeholdings.coverstar.data.CommentItem;

import java.util.ArrayList;
import java.util.Map;

public class CommentHelper {
    private static final String TAG = "CommentHelper";
    private static volatile CommentHelper instance;
    private final static Object lockObject = new Object();

    public static final String FIRESTORE_TB_COMMENT = "COMMENT";
    public static final String LIST_COLLECTION_NAME = "LIST";

    public static final String FIELDNAME_ID = "id";
    public static final String FIELDNAME_CONTESTUSERID = "contestUserId";
    public static final String FIELDNAME_USERID = "userId";
    public static final String FIELDNAME_USERIMAGEPATH = "userImagePath";
    public static final String FIELDNAME_USERNICKNAME = "userNickName";
    public static final String FIELDNAME_COMMENTDATE = "commentDate";
    public static final String FIELDNAME_COMMENT = "comment";
    public static final String FIELDNAME_ISFIXED = "isFixed";
    public static final String FIELDNAME_ISREMOVED = "isRemoved";
    public static final String FIELDNAME_LIKES = "likes";
    public static final String FIELDNAME_UNLIKES = "unLikes";
    public static final String FIELDNAME_COMMENTS = "comments";
    public static final String FIELDNAME_REPORTS = "reports";


    public static final String COMMENT_TIME_FORMAT = "yyyymmddhhmmss";
    public static CommentHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new CommentHelper();
                }
            }
        }

        return instance;
    }

    public interface ICommentEventListener {
        public void onCommentListLoaded(ArrayList<CommentItem> commentList);
    }
    public CollectionReference getCommentListRef(String castCode) {
        return FireBaseHelper.getSingleInstance().getDatabase()
                .collection(FIRESTORE_TB_COMMENT)
                .document(castCode)
                .collection(LIST_COLLECTION_NAME);
    }


    public void getCommentList(String castCode, ICommentEventListener eventListener) {
        getCommentListRef(castCode).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<CommentItem> itemList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        CommentItem item = getCommentItem(doc.getData());
                        if (item.isRemoved) {
                            continue;
                        }

                        item.id = doc.getId();
                        itemList.add(item);
                    }

                }
                eventListener.onCommentListLoaded(itemList);
            }
        });
    }

    private CommentItem getCommentItem(Map<String, Object> data) {
        CommentItem item = new CommentItem();
        try {
            item.id = (String) data.get(FIELDNAME_ID);
            item.userId = (String) data.get(FIELDNAME_USERID);
            item.contestUserId = (String) data.get(FIELDNAME_CONTESTUSERID);
            item.userImagePath = (String) data.get(FIELDNAME_USERIMAGEPATH);
            item.userNickName = (String) data.get(FIELDNAME_USERNICKNAME);
            item.commentDate = (String) data.get(FIELDNAME_COMMENTDATE);
            item.comment = (String) data.get(FIELDNAME_COMMENT);
            if (data.containsKey(FIELDNAME_ISFIXED)) {
                item.isFixed = (boolean) data.get(FIELDNAME_ISFIXED);
            }

            if (data.containsKey(FIELDNAME_ISREMOVED)) {
                item.isRemoved = (boolean) data.get(FIELDNAME_ISREMOVED);
            }

            if (data.containsKey(FIELDNAME_LIKES)) {
                item.likes = (ArrayList<String>) data.get(FIELDNAME_LIKES);
            }

            if (data.containsKey(FIELDNAME_UNLIKES)) {
                item.unLikes = (ArrayList<String>) data.get(FIELDNAME_UNLIKES);
            }

            if (data.containsKey(FIELDNAME_COMMENTS)) {
                item.comments = (ArrayList<String>) data.get(FIELDNAME_COMMENTS);
            }
        } catch (Exception e) {
            DebugLogger.exception(e);
        }

        return item;
    }
}
