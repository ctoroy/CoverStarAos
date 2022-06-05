package com.shinleeholdings.coverstar.chatting;

import android.text.TextUtils;

import com.shinleeholdings.coverstar.util.LoginHelper;

import java.util.Calendar;

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
	private FILE_MESSAGE_STATE fileState = FILE_MESSAGE_STATE.NORMAL;

	private long currentByte = 0;
	private long totalByte = 0;


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

	public void setCurrentByte(long currentUploadedByte) {
		this.currentByte = currentUploadedByte;
	}

	public void setTotalByte(long totalByte) {
		this.totalByte = totalByte;
	}

	public boolean isExpired() {
		if (chatItem == null || TextUtils.isEmpty(chatItem.expireDate)) {
			return true;
		}

		String[] dateInfo = chatItem.expireDate.split("\\.");

		String year = dateInfo[0];
		String month = dateInfo[1];
		String day = dateInfo[2];

		Calendar expireDate = Calendar.getInstance();
		expireDate.set(Integer.parseInt(year), (Integer.parseInt(month) - 1), Integer.parseInt(day));

		Calendar currentDate = Calendar.getInstance();
		return expireDate.before(currentDate);
	}

	public String getExpireDateText() {
		if (chatItem == null || TextUtils.isEmpty(chatItem.expireDate)) {
			return "";
		}

		return chatItem.expireDate;
	}

	public String getTotalFileSizeText() {
		return "";
//		return UploadHelper.getSingleInstance().getFileSizeText(Long.parseLong(chatItem.fileSize));
	}

	public String uploadStateText() {
		String currentByteText = "";
		String totalByteText = "";
		String unit = "";

//		if (totalByte < UploadHelper.ONE_MB_SIZE_IN_BYTE) {
//			unit = "KB";
//			currentByteText = UploadHelper.getSingleInstance().bytesToKB(currentByte);
//			totalByteText = UploadHelper.getSingleInstance().bytesToKB(totalByte);
//		} else {
//			unit = "MB";
//			currentByteText = UploadHelper.getSingleInstance().bytesToMB(currentByte);
//			totalByteText = UploadHelper.getSingleInstance().bytesToMB(totalByte);
//		}

		return currentByteText + " / " + totalByteText + " " + unit;
	}

	public int getPercent() {
		try {
			return (int) (((long)currentByte * 100) / (long)totalByte);
		} catch (Exception e) {
		}
		return 0;
	}

	public String getFilePath() {
		return chatItem.filePath;
	}

	public String getFileName() {
		return chatItem.filename;
	}

	/**
	 * 전송상태 : 전송중, 전송 실패, 전송 완료
	 *
	 * @author sdk
	 *
	 */
	public enum SENDSTATE {
		SUCCESS, FAIL, SENDING, UPLOADING
	}

	public enum FILE_MESSAGE_STATE {
		NORMAL, DOWNLOADING;
	}

	public FILE_MESSAGE_STATE getFileState() {
		return fileState;
	}

	public void setFileState(FILE_MESSAGE_STATE fileState) {
		this.fileState = fileState;
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

	public String getDownloadUrl() {
		String filePath = chatItem.filename;
//
//		if (filePath.contains("http://") == false && filePath.contains("https://") == false) {
//			filePath = ServerAPIConstants.FILE_SERVER_PATH + File.separator + filePath;
//		}

		return filePath;
	}

	public String getImagePath() {
		return "";
//		return UserStatusHelper.getSingleInstance().getUserStatus(chatItem.user_id).getPhoto();
	}

	public void setFilePath(String filePath) {
		chatItem.filePath = filePath;
	}

	public String getJsonStringData() {
		return chatItem.getJsonStringData();
	}
}
