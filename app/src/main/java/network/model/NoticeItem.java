package network.model;

import com.google.gson.annotations.SerializedName;

public class NoticeItem {
    @SerializedName("title") public String title;
    @SerializedName("imagePath") public String imagePath;
    @SerializedName("contents") public String contents;
    @SerializedName("noticeDate") public String noticeDate;
    public boolean isSelected = false;
}
