package com.shinleeholdings.coverstar.chatting;

import com.shinleeholdings.coverstar.util.LoginHelper;

/**
 * 채팅 항목 아이템
 * 
 * @author sdk
 * 
 */
public class ChattingItem {
	private String msgKey; // 파이어베이스에 저장되어있는 메세지 키값
	private ChatItem chatItem;
	private String chattingId;

	private SENDSTATE sendState;

	public ChattingItem(ChatItem item, String chattingId, SENDSTATE state) {
		msgKey = "";
		chatItem = item;
		this.chattingId = chattingId;
		sendState = state;
	}

	public ChattingItem(String msgKey, ChatItem item, String chattingId, SENDSTATE state) {
		this.msgKey = msgKey;
		chatItem = item;
		this.chattingId = chattingId;
		sendState = state;
	}

	public String getChattingId() {
		return chattingId;
	}

	/**
	 * 전송상태 : 전송중, 전송 실패, 전송 완료
	 *
	 * @author sdk
	 *
	 */
	public enum SENDSTATE {
		SUCCESS, FAIL, SENDING
	}

	public SENDSTATE getSendState() {
		return sendState;
	}

	public void setSendState(SENDSTATE sendState) {
		this.sendState = sendState;
	}

	public ChatItem getChatItem() {
		return chatItem;
	}

	public boolean isMemberChangeMessage() {
		return false;
	}

	public String getMsgKey() {
		return msgKey;
	}

	public String getTimeStampKey() {
		return chatItem.key;
	}

	public boolean isMyChat() {
		return  LoginHelper.getSingleInstance().getLoginUserId().equals(chatItem.user_id);
	}

	public String getMessageTimeDisplayText() {
		// TODO
		return "";
//		return TimeHelper.getChattingMessageTime(chatItem.cdate);
	}

	public String getMessageDateText() {
		// TODO
		return "";
//		return TimeHelper.formattedDate(chatItem.cdate, "yyyyMMddHHmmssSSS", "yyyy년 MM월 dd일");
	}

	public String getMessageDateText2() {
		// TODO
		return "";
//		return TimeHelper.formattedDate(chatItem.cdate, "yyyyMMddHHmmssSSS", "yyyy년 M월 d일");
	}

	public String getImagePath() {
		// TODO 사용자 이름
		return "";
//		return UserStatusHelper.getSingleInstance().getUserStatus(chatItem.user_id).getPhoto();
	}

	public String getJsonStringData() {
		return chatItem.getJsonStringData();
	}
}
