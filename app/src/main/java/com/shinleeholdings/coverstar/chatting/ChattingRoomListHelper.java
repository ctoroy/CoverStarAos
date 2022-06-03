package com.shinleeholdings.coverstar.chatting;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.util.FireBaseHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

import network.model.CoverStarUser;

public class ChattingRoomListHelper {
    private static final String TAG = "ChattingRoomListHelper";
    private static volatile ChattingRoomListHelper instance;
    private final static Object lockObject = new Object();

    // TODO 채팅방 목록 구조 허브톡 참고해서 다시 확인 필요

    // 채팅방 목록
    // CHATLIST : collection
    //      ROOMINFO(auto id) : document
    //          eventMessage : String
    //          eventTime : Long
    //          memberIdList : Array<String>
    //          memberNickNameList : Array<String>
    private static final String FIRESTORE_TB_CHATTINGLIST = "CHATLIST";
    private static final String FIELDNAME_MEMBERID_LIST = "memberIdList";
    private static final String FIELDNAME_MEMBERNICKNAME_LIST = "memberNickNameList";
    private static final String FIELDNAME_EVENT_TIME = "eventTime";
    private static final String FIELDNAME_EVENT_MESSAGE = "eventMessage";

    public static ChattingRoomListHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new ChattingRoomListHelper();
                }
            }
        }

        return instance;
    }

    private CollectionReference getChattingListRef() {
        return FireBaseHelper.getSingleInstance().getDatabase().collection(FIRESTORE_TB_CHATTINGLIST);
    }

    public void getChattingRoomListRef(String userId) {
        // TODO
        getChattingListRef().whereArrayContains(FIELDNAME_MEMBERID_LIST, userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });
    }


    public void startChattingRoom(MainActivity activity, CoverStarUser myInfo, CoverStarUser targetUser) {
        ProgressDialogHelper.show(activity);

        // TODO 기존에 채팅방이 있었는지 체크 필요
//        getChattingListRef()
//                .whereArrayContains(FIELDNAME_MEMBERID_LIST., myInfo.userId)
//                .whereArrayContains(FIELDNAME_MEMBERID_LIST, myInfo.userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//            }
//        });
        ArrayList<String> memberIdList = new ArrayList<String>();
        memberIdList.add(myInfo.userId);
        memberIdList.add(targetUser.userId);

        ArrayList<String> memberNickNameList = new ArrayList<String>();
        memberNickNameList.add(myInfo.nickName);
        memberNickNameList.add(targetUser.nickName);

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(FIELDNAME_EVENT_MESSAGE, "");
        valueMap.put(FIELDNAME_EVENT_TIME, Util.getCurrentTimeToFormat(AppConstants.COMMON_TIME_FORMAT));
        valueMap.put(FIELDNAME_MEMBERID_LIST, memberIdList);
        valueMap.put(FIELDNAME_MEMBERNICKNAME_LIST, memberNickNameList);

        getChattingListRef().add(valueMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                ProgressDialogHelper.dismiss();
                if (task.isSuccessful()) {
                    DocumentReference ref = task.getResult();
                    startChattingRoom(activity, ref.getId());
                }
            }
        });
    }

    public void startChattingRoom(MainActivity activity, String chattingRoomId) {
        // TODO 채팅방 진입
    }
}
