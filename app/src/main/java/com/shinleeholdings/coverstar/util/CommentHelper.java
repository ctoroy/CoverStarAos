package com.shinleeholdings.coverstar.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
    public static final String FIELDNAME_LIKES = "likes";
    public static final String FIELDNAME_UNLIKES = "unLikes";
    public static final String FIELDNAME_COMMENTS = "comments";
    public static final String FIELDNAME_REPORTS = "reports";

    public static final String FIELDNAME_MESSAGE_DATE = "messageDate";
    public static final String FIELDNAME_MESSAGE = "message";

    public static final String FIELDNAME_COMMENTID = "commentId";

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
        void onCommentListLoaded(ArrayList<CommentItem> commentList);
        void onReplyListLoaded(ArrayList<ReplyItem> replyList);
    }

    public HashMap<String, Object> getDefaultHashMap() {
        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(CommentHelper.FIELDNAME_USERID, LoginHelper.getSingleInstance().getLoginUserId());
        valueMap.put(CommentHelper.FIELDNAME_USERIMAGEPATH, LoginHelper.getSingleInstance().getLoginUserImagePath());
        valueMap.put(CommentHelper.FIELDNAME_USERNICKNAME, LoginHelper.getSingleInstance().getLoginUserNickName());
        valueMap.put(CommentHelper.FIELDNAME_LIKES, new ArrayList<String>());
        valueMap.put(CommentHelper.FIELDNAME_UNLIKES, new ArrayList<String>());
        valueMap.put(CommentHelper.FIELDNAME_REPORTS, new ArrayList<String>());

        return valueMap;
    }

    public CollectionReference getCommentListRef(String castCode) {
        return FireBaseHelper.getSingleInstance().getDatabase()
                .collection(FIRESTORE_TB_COMMENT)
                .document(castCode)
                .collection(LIST_COLLECTION_NAME);
    }

    public void getCommentList(String castCode, ICommentEventListener eventListener) {
        getCommentListRef(castCode)
                .orderBy(FIELDNAME_MESSAGE_DATE, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DebugLogger.i("commentTest getCommentList onComplete : " + task.isSuccessful());
                ArrayList<CommentItem> itemList = new ArrayList<>();
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
        HashMap<String, Object> valueMap = getDefaultHashMap();
        valueMap.put(CommentHelper.FIELDNAME_CONTESTUSERID, contest.castId);
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE_DATE, Util.getCurrentTimeToFormat(CommentHelper.COMMENT_TIME_FORMAT));
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE, comment);
        valueMap.put(CommentHelper.FIELDNAME_COMMENTS, new ArrayList<String>());
        getCommentListRef(contest.castCode).add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                DebugLogger.i("commentTest writeCommentItem writeComplete success : " + task.isSuccessful());
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
            item.setDefaultInfo(data);

            if (data.containsKey(FIELDNAME_COMMENTS)) {
                item.comments = (ArrayList<String>) data.get(FIELDNAME_COMMENTS);
            }

        } catch (Exception e) {
            DebugLogger.exception(e);
        }

        return item;
    }

    // TODO 답글 작업 시작

    private CollectionReference getReplyListRef(String castCode, String commentId) {
        return getCommentListRef(castCode)
                .document(commentId)
                .collection(RETRY_COLLECTION_NAME);
    }

    public void getReplyList(String castCode, String commentId, ICommentEventListener eventListener) {
        getReplyListRef(castCode, commentId)
                .orderBy(FIELDNAME_MESSAGE_DATE, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DebugLogger.i("commentTest getReplyList onComplete castCode : " + castCode + " , commentId : " + commentId);
                ArrayList<ReplyItem> itemList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        itemList.add(getReplyItem(doc));
                    }
                }
                eventListener.onReplyListLoaded(itemList);
            }
        });
    }

    public void writeReplyItem(ContestData contest, String commentId, String reply) {
        DebugLogger.i("commentTest writeReplyItem : " + reply);
        HashMap<String, Object> valueMap = getDefaultHashMap();
        valueMap.put(CommentHelper.FIELDNAME_COMMENTID, commentId);
        valueMap.put(CommentHelper.FIELDNAME_CONTESTUSERID, contest.castId);
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE_DATE, Util.getCurrentTimeToFormat(CommentHelper.COMMENT_TIME_FORMAT));
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE, reply);

        getReplyListRef(contest.castCode, commentId).add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                DebugLogger.i("commentTest writeReplyItem writeComplete success : " + task.isSuccessful());
            }
        });
    }

    public void deleteReplyItem(String castCode, ReplyItem replyItem) {
        getReplyListRef(castCode, replyItem.commentId).document(replyItem.id).delete();
    }

    public void updateCommentItem(String castCode, ReplyItem replyItem, HashMap<String, Object> valueMap) {
        getReplyListRef(castCode, replyItem.commentId).document(replyItem.id).update(valueMap);
    }

    public ReplyItem getReplyItem(DocumentSnapshot doc) {
        ReplyItem item = new ReplyItem();
        try {
            Map<String, Object> data = doc.getData();
            item.id = doc.getId();
            item.commentId = (String) data.get(FIELDNAME_COMMENTID);
            item.setDefaultInfo(data);
        } catch (Exception e) {
            DebugLogger.exception(e);
        }
        return item;
    }

}
