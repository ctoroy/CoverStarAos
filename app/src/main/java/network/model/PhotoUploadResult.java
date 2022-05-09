package network.model;

import com.google.gson.annotations.SerializedName;

public class PhotoUploadResult extends BaseResponse {

	@SerializedName("data")
	private ImageUrlData imageUrlData;

	private class ImageUrlData {

		@SerializedName("imgUrl")
		private String imageUrl;
	}

	public String getImageUrl() {
		return imageUrlData.imageUrl;
	}
}

