package com.shinleeholdings.coverstar.util;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    public static final String FIELDNAME_LIKES = "likes";
    public static final String FIELDNAME_UNLIKES = "unLikes";
    public static final String FIELDNAME_COMMENT_COUNT = "commentCount";
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

    public interface IFireStoreActionCompleteListener {
        void onCompleted();
    }

    public interface ICommentEventListener {
        void onCommentListLoaded(ArrayList<CommentItem> commentList);
    }

    public interface IReplyLoadListener {
        void onReplyListLoaded(CommentItem comment, ArrayList<ReplyItem> replyList);
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

    public void writeCommentItem(ContestData contest, String comment, IFireStoreActionCompleteListener listener) {
        DebugLogger.i("commentTest writeCommentItem : " + comment);
        HashMap<String, Object> valueMap = getDefaultHashMap();
        valueMap.put(CommentHelper.FIELDNAME_CONTESTUSERID, contest.castId);
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE_DATE, Util.getCurrentTimeToFormat(CommentHelper.COMMENT_TIME_FORMAT));
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE, comment);
        valueMap.put(CommentHelper.FIELDNAME_COMMENT_COUNT, 0);
        getCommentListRef(contest.castCode).add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                DebugLogger.i("commentTest writeCommentItem writeComplete success : " + task.isSuccessful());
                listener.onCompleted();
            }
        });
    }

    public void deleteCommentItem(String castCode, CommentItem commentItem, IFireStoreActionCompleteListener listener) {
        getCommentListRef(castCode).document(commentItem.id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DebugLogger.i("commentTest deleteCommentItem complete success : " + task.isSuccessful());
                listener.onCompleted();
            }
        });
    }

    public void updateCommentItem(String castCode, CommentItem commentItem, HashMap<String, Object> valueMap, IFireStoreActionCompleteListener listener) {
        getCommentListRef(castCode).document(commentItem.id).update(valueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DebugLogger.i("commentTest updateCommentItem complete success : " + task.isSuccessful());
                listener.onCompleted();
            }
        });
    }

    public CommentItem getCommentItem(Map<String, Object> data) {
        CommentItem item = new CommentItem();
        try {
            item.setDefaultInfo(data);
            item.commentCount = (int) data.get(FIELDNAME_COMMENT_COUNT);

        } catch (Exception e) {
            DebugLogger.exception(e);
        }

        return item;
    }

    public CollectionReference getReplyListRef(String castCode, String commentId) {
        return getCommentListRef(castCode)
                .document(commentId)
                .collection(RETRY_COLLECTION_NAME);
    }

    public void getReplyList(String castCode, CommentItem comment, IReplyLoadListener eventListener) {
        getReplyListRef(castCode, comment.id)
                .orderBy(FIELDNAME_MESSAGE_DATE, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DebugLogger.i("commentTest getReplyList onComplete castCode : " + castCode + " , commentId : " + comment.id);
                ArrayList<ReplyItem> itemList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        Map<String, Object> data = doc.getData();
                        String id = doc.getId();
                        itemList.add(getReplyItem(id, data));
                    }
                }
                eventListener.onReplyListLoaded(comment, itemList);
            }
        });
    }

    public void writeReplyItem(ContestData contest, String commentId, String reply, IFireStoreActionCompleteListener listener) {
        DebugLogger.i("commentTest writeReplyItem : " + reply);
        HashMap<String, Object> valueMap = getDefaultHashMap();
        valueMap.put(CommentHelper.FIELDNAME_COMMENTID, commentId);
        valueMap.put(CommentHelper.FIELDNAME_CONTESTUSERID, contest.castId);
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE_DATE, Util.getCurrentTimeToFormat(CommentHelper.COMMENT_TIME_FORMAT));
        valueMap.put(CommentHelper.FIELDNAME_MESSAGE, reply);

        getReplyListRef(contest.castCode, commentId).add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                DebugLogger.i("commentTest writeReplyItem success : " + task.isSuccessful());
                listener.onCompleted();
            }
        });
        getCommentListRef(contest.castCode).document(commentId).update(FIELDNAME_COMMENT_COUNT, FieldValue.increment(1));
    }

    public void deleteReplyItem(String castCode, ReplyItem replyItem, IFireStoreActionCompleteListener listener) {
        getReplyListRef(castCode, replyItem.commentId).document(replyItem.id).delete();
        getCommentListRef(castCode).document(replyItem.commentId).update(FIELDNAME_COMMENT_COUNT, FieldValue.increment(-1)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DebugLogger.i("commentTest deleteReplyItem success : " + task.isSuccessful());
                listener.onCompleted();
            }
        });
    }

    public void updateReplyItem(String castCode, ReplyItem replyItem, HashMap<String, Object> valueMap, IFireStoreActionCompleteListener listener) {
        getReplyListRef(castCode, replyItem.commentId).document(replyItem.id).update(valueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onCompleted();
            }
        });
    }

    public ReplyItem getReplyItem(String id, Map<String, Object> data) {
        ReplyItem item = new ReplyItem();
        try {
            item.commentId = (String) data.get(FIELDNAME_COMMENTID);
            item.setDefaultInfo(data);
        } catch (Exception e) {
            DebugLogger.exception(e);
        }
        return item;
    }

}
