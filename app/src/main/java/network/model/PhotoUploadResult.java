package network.model;

import com.google.gson.annotations.SerializedName;

public class PhotoUploadResult extends BaseResponse {

	@SerializedName("data")
	private String chattingRoomId;
}
