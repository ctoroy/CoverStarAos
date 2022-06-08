package network.model;

import com.google.gson.annotations.SerializedName;

public class ChatCreate {
	@SerializedName("chattingId") private String chattingRoomId;

	public String getChattingRoomId() {
		return chattingRoomId;
	}
}
