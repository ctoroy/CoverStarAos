package com.shinleeholdings.coverstar.util;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentItem;
import com.shinleeholdings.coverstar.data.ContestData;
import com.shinleeholdings.coverstar.data.ReplyItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentHelper {
    private static final String TAG = "CommentHelper";
    private static volatile CommentHelper instance;
    private final static Object lockObject = new Object();

    public static final String FIRESTORE_TB_COMMENT = "COMMENT";
    public static final String LIST_COLLECTION_NAME = "LIST";
    public static final String RETRY_COLLECTION_NAME = "RETRY";

    public static final String FIELDNAME_CONTESTUSERID = "contestUserId";
    public static final String FIELDNAME_USERID = "userId";
    public static final String FIELDNAME_USERIMAGEPATH = "userImagePath";
    public static final String FIELDNAME_USERNICKNAME = "userNickName";
    public static final String FIELDNAME_COMMENTDATE = "commentDate";
    public static final String FIELDNAME_COMMENT = "comment";
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
        public void onReplyListLoaded(ArrayList<ReplyItem> replyList);
    }

    public CollectionReference getCommentListRef(String castCode) {
        return FireBaseHelper.getSingleInstance().getDatabase()
                .collection(FIRESTORE_TB_COMMENT)
                .document(castCode)
                .collection(LIST_COLLECTION_NAME);
    }

    public void getCommentList(String castCode, ICommentEventListener eventListener) {
        getCommentListRef(castCode).orderBy(FIELDNAME_COMMENTDATE, Query.Direction.DESCENDING).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<CommentItem> itemList = new ArrayList<>();
                DebugLogger.i("commentTest getCommentList onComplete : " + task.isSuccessful());
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        CommentItem item = getCommentItem(doc.getData());
                        item.id = doc.getId();
                        itemList.add(item);
                    }

                }
                eventListener.onCommentListLoaded(itemList);
            }
        });
    }

    public void writeCommentItem(ContestData contest, String comment) {
        DebugLogger.i("commentTest writeCommentItem : " + comment);
        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(CommentHelper.FIELDNAME_CONTESTUSERID, contest.castId);
        valueMap.put(CommentHelper.FIELDNAME_USERID, LoginHelper.getSingleInstance().getLoginUserId());
        valueMap.put(CommentHelper.FIELDNAME_USERIMAGEPATH, LoginHelper.getSingleInstance().getLoginUserImagePath());
        valueMap.put(CommentHelper.FIELDNAME_USERNICKNAME, LoginHelper.getSingleInstance().getLoginUserNickName());
        valueMap.put(CommentHelper.FIELDNAME_COMMENTDATE, Util.getCurrentTimeToFormat(CommentHelper.COMMENT_TIME_FORMAT));
        valueMap.put(CommentHelper.FIELDNAME_COMMENT, comment);
        valueMap.put(CommentHelper.FIELDNAME_LIKES, new ArrayList<String>());
        valueMap.put(CommentHelper.FIELDNAME_UNLIKES, new ArrayList<String>());
        valueMap.put(CommentHelper.FIELDNAME_COMMENTS, new ArrayList<String>());
        valueMap.put(CommentHelper.FIELDNAME_REPORTS, new ArrayList<String>());
        getCommentListRef(contest.castCode).add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                DebugLogger.i("commentTest writeComplete success : " + task.isSuccessful());
            }
        });
    }

    public void deleteCommentItem(String castCode, CommentItem commentItem) {
        getCommentListRef(castCode).document(commentItem.id).delete();
    }

    public void updateCommentItem(String castCode, CommentItem commentItem, HashMap<String, Object> valueMap) {
        getCommentListRef(castCode).document(commentItem.id).update(valueMap);
    }

    public CommentItem getCommentItem(Map<String, Object> data) {
        CommentItem item = new CommentItem();
        try {
            item.userId = (String) data.get(FIELDNAME_USERID);
            item.contestUserId = (String) data.get(FIELDNAME_CONTESTUSERID);
            item.userImagePath = (String) data.get(FIELDNAME_USERIMAGEPATH);
            item.userNickName = (String) data.get(FIELDNAME_USERNICKNAME);
            item.commentDate = (String) data.get(FIELDNAME_COMMENTDATE);
            item.comment = (String) data.get(FIELDNAME_COMMENT);

            if (data.containsKey(FIELDNAME_LIKES)) {
                item.likes = (ArrayList<String>) data.get(FIELDNAME_LIKES);
            }

            if (data.containsKey(FIELDNAME_UNLIKES)) {
                item.unLikes = (ArrayList<String>) data.get(FIELDNAME_UNLIKES);
            }

            if (data.containsKey(FIELDNAME_COMMENTS)) {
                item.comments = (ArrayList<String>) data.get(FIELDNAME_COMMENTS);
            }

            if (data.containsKey(FIELDNAME_REPORTS)) {
                item.reports = (ArrayList<String>) data.get(FIELDNAME_REPORTS);
            }
        } catch (Exception e) {
            DebugLogger.exception(e);
        }

        return item;
    }


    public CollectionReference getReplyListRef(String castCode, String commentId) {
        return FireBaseHelper.getSingleInstance().getDatabase()
                .collection(FIRESTORE_TB_COMMENT)
                .document(castCode)
                .collection(LIST_COLLECTION_NAME)
                .document(commentId)
                .collection(RETRY_COLLECTION_NAME)
    }

    public void getReplyList(String castCode, String commentId, ICommentEventListener eventListener) {
        getReplyListRef(castCode)
                .orderBy(FIELDNAME_COMMENTDATE, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DebugLogger.i("commentTest getReplyList onComplete castCode : " + castCode + " , commentId : " + commentId);
                ArrayList<ReplyItem> itemList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        ReplyItem item = getReplyItem(doc.getData());
                        item.id = doc.getId();
                        itemList.add(item);
                    }
                }

                eventListener.onReplyListLoaded(itemList);
            }
        });
    }

    public void writeReplyItem(ContestData contest, String commentId, String reply) {
        // TODO

    }

    public ReplyItem getReplyItem(Map<String, Object> data) {
        ReplyItem item = new ReplyItem();
        // TODO
        return item;
    }

}
