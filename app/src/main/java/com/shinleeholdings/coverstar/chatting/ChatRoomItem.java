package com.shinleeholdings.coverstar.chatting;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.LoginHelper;

import java.util.ArrayList;

public class ChatRoomItem implements Comparable<ChatRoomItem> {
    private String chatId;

    private String roomName;
    private String messageDate;

    private String lastMessage;
    private String lastMessageKey;
    private ArrayList<String> deleteInfoList;

    public String getLastMessageKey() {
        return lastMessageKey;
    }

    public void setLastMessageKey(String lastMessageKey) {
        this.lastMessageKey = lastMessageKey;
    }

    public ArrayList<String> getDeleteInfoList() {
        return deleteInfoList;
    }

    public void setDeleteInfoList(ArrayList<String> deleteInfoList) {
        this.deleteInfoList = deleteInfoList;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public long getBadgeCount() {
        return BadgeManager.getSingleInstance().getChattingRoomBadge(chatId);
    }

    public String getDisplayRoomName() {
        ArrayList<ChattingRoomMember> memberList = ChatRoomListHelper.getSingleInstance().getChattingRoomMember(chatId);
        if (memberList != null) {
            StringBuilder sb = new StringBuilder();
            for (ChattingRoomMember member : memberList) {
                if (member.getUserId().equals(LoginHelper.getSingleInstance().getLoginUserId()) == false) {
                    sb.append(member.getUserNickName());
                    sb.append(",");
                }
            }

            String value = sb.toString();
            if (TextUtils.isEmpty(value) == false) {
                roomName = value.substring(0, value.length() - 1);
                if (roomName.length() > 50) {
                    roomName = roomName.substring(0, 49);
                }
            }
        }

        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLastMessage() {
        if (TextUtils.isEmpty(lastMessage) == false && lastMessage.equals("삭제한 메세지 입니다.") == false) {
            if (deleteInfoList != null && deleteInfoList.size() > 0) {
                String loginUserSeq = LoginHelper.getSingleInstance().getLoginUserId();
                for (String userSeq : deleteInfoList) {
                    if (userSeq.equals(loginUserSeq)) {
                        DebugLogger.i("test", "lastMessageTest getLastMessage deleteInfoList has mySeq");
                        // 삭제한 사용자 목록에 내 아이디가 있을 경우
                        lastMessage = "삭제한 메세지 입니다.";
                        break;
                    }
                }
            }
        }

        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public int getMemberCount() {
        ArrayList<ChattingRoomMember> memberList = ChatRoomListHelper.getSingleInstance().getChattingRoomMember(chatId);

        if (memberList == null) {
            return 0;
        }
        return memberList.size();
    }

    public ArrayList<ChattingRoomMember> getMemberList() {
        return ChatRoomListHelper.getSingleInstance().getChattingRoomMember(chatId);
    }

    public String getChattingMemberPhoto() {
        ArrayList<ChattingRoomMember> memberList = ChatRoomListHelper.getSingleInstance().getChattingRoomMember(chatId);
        if (memberList != null) {
            for (ChattingRoomMember member : memberList) {
                if (member.getUserId().equals(LoginHelper.getSingleInstance().getLoginUserId()) == false) {
                    return member.getUserPhoto();
                }
            }
        }

        return "";
    }

    @Override
    public int compareTo(@NonNull ChatRoomItem objectItem) {
        String myDate = messageDate;
        String targetDate = objectItem.messageDate;

        if(TextUtils.isEmpty(myDate) && TextUtils.isEmpty(targetDate)) {
            return 0;
        }

        if (TextUtils.isEmpty(myDate)) {
            return -1;
        }
        if (TextUtils.isEmpty(targetDate)) {
            return 1;
        }

        long my = Long.parseLong(messageDate);
        long target = Long.parseLong(objectItem.messageDate);

        if (my > target) {
            return -1;
        } else if (my < target) {
            return 1;
        }
        return 0;
    }
}
