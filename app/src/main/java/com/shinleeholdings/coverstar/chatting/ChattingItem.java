package com.shinleeholdings.coverstar.chatting;

import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.Util;

/**
 * 채팅 항목 아이템
 * 
 * @author sdk
 * 
 */
public class ChattingItem {
	private final String msgKey; // 파이어베이스에 저장되어있는 메세지 키값
	private final ChatItem chatItem;
	private final String chattingId;

	private SENDSTATE sendState;

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
		return Util.getChattingMessageTime(chatItem.cdate);
	}

	public String getTimeLineDateText() {
		return Util.getChattingTimeLineDateValue(chatItem.cdate, "yyyy년 MM월 dd일");
	}

	public String getImagePath() {
		return chatItem.user_photo;
	}

	public String getJsonStringData() {
		return chatItem.getJsonStringData();
	}
}
