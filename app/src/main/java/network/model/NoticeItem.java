package network.model;

import com.google.gson.annotations.SerializedName;

public class NoticeItem {
    @SerializedName("id") public int id;
    @SerializedName("createdat") public String createdat; // "2022-05-24 13:02:08.425205"
    @SerializedName("updatedat") public String updatedat; // "2022-05-24 13:02:08.425205"
    @SerializedName("videoUrl") public String videoUrl;
    @SerializedName("title") public String title;
    @SerializedName("content") public String content;
    @SerializedName("type") public int type;
    @SerializedName("view_count") public int viewCount;
    @SerializedName("like_count") public int likeCount;
    @SerializedName("thumbnail") public String thumbnail;
    public boolean isSelected = false;
}
