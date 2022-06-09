package com.shinleeholdings.coverstar.chatting;


/**
 * 채팅 DataBase 상수들
 * 
 * @author sdk
 * 
 */
public final class ChattingConstants {
	public static final String MSG_TYPE_MESSAGE = "1";

	public static final String TB_CHAT_INFO = "chat_info";
	public static final String TB_CHAT_LIST = "chat_list";
	public static final String TB_CHAT_MESSAGE = "message";

	public static final String TEMP_COLLECTION_NAME = "a";

	public static final String FIELDNAME_BADGE_CNT = "badge_cnt";

	public static final String FIELDNAME_USER_ID = "user_id";
	public static final String FIELDNAME_USER_NAME = "user_name";
	public static final String FIELDNAME_USER_PHOTO = "user_photo";



	public static final String MESSAGE_QUEUE_TABLE_NAME = "messagequeue";

	// db Table 인덱스
	public static final int INDEX_MSGNUM = 0;
	public static final int INDEX_CHATTING_ID = 1;
	public static final int INDEX_KEY = 2;
	public static final int INDEX_DATA = 3;

	// db Table Field 명
	public static final String FIELD_MSGNUM = "MsgNum";
	public static final String FIELD_CHATTING_ID = "ChattingId";
	public static final String FIELD_KEY = "Key";
	public static final String FIELD_DATA = "Data";

	public static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + MESSAGE_QUEUE_TABLE_NAME + " (" +
																						FIELD_MSGNUM + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
																						FIELD_CHATTING_ID + " TEXT NOT NULL, " +
																						FIELD_KEY + " TEXT NOT NULL, " +
																						FIELD_DATA + " TEXT NOT NULL);";

	public static final String SELECTION = "(" + FIELD_CHATTING_ID + "='%s' AND " + FIELD_KEY + "='%s')";
	public static final String CHATTINGID_SELECTION = FIELD_CHATTING_ID + "='%s'";
	public static final String QUERY_HASITEM = "select count(*) from " + MESSAGE_QUEUE_TABLE_NAME + " where " + SELECTION;
}
