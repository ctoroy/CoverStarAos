package com.shinleeholdings.coverstar.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import network.model.LoginUserData;

public class ContestData implements Parcelable {
    //+:경연 *:이벤트
//	public String castCode; //+*방키 방장아이디+yyyymmddhhmmss ctoroy20210726162622
//	public String castId; //+*방장 아이디
//	public Integer watchCnt; //플레이수
//	public String castTitle; //+*타이틀
//	public Integer category;
//	public Integer castPath; contestId
//	public String nickName; //+*참가자명
//	public String profileImage; //+*썸네일 이미지
//	public Integer castType; //0:+,1:*
//	public String castStartDate; //+*시작시간
//	public String castEndDate;  //+*종료시간
//	public Integer episode; //+*댓글수
//	public String logoImage; //+*원곡가수명
//	public String sortBig; //+*한마디
//	public String sortMid; //*이벤트총상금
//	public String sortSmall; //*이벤트타이틀
//	public String location; //영상 URL
//	public String store; //*이벤트발표일
//	public String product; //*이벤트 이미지
//	public String likes;
//	public String accumWatchCnt;
    @SerializedName("castId") public String castId;  //+*방장 아이디
    @SerializedName("castCode") public String castCode; //+*방키 방장아이디+yyyymmddhhmmss ctoroy20210726162622
    @SerializedName("watchCnt") public int watchCnt; //플레이수
    @SerializedName("castTitle") public String castTitle; //+*타이틀

    @SerializedName("category") public int category; // 0 커버스타 상단, 1: 이벤트 상단, 2 경연 참가
    @SerializedName("castPath") public int castPath; //contestId

    @SerializedName("nickName") public String nickName; //+*참가자명
    @SerializedName("profileImage") public String profileImage; //+*썸네일 이미지
    @SerializedName("castType") public int castType; //0: 커버스타 참가,1: 이벤트 참가

    @SerializedName("castStartDate") public String castStartDate; //+*시작시간
    @SerializedName("castEndDate") public String castEndDate; //+*종료시간
    @SerializedName("episode") public int episode; //
    @SerializedName("logoImage") public String logoImage; //+*원곡가수명
    @SerializedName("sortBig") public String sortBig; //+*한마디
    @SerializedName("sortMid") public long sortMid; //*이벤트총상금
    @SerializedName("sortSmall") public String sortSmall; //*이벤트타이틀
    @SerializedName("location") public String location; //영상 URL
    @SerializedName("store") public String store; //*이벤트발표일
    @SerializedName("product") public String product; //*이벤트 이미지
    @SerializedName("likes") public String likes; // 총 좋아요 카운트
    @SerializedName("accumWatchCnt") public String accumWatchCnt; // 사용자 이미지, getRoom에서는 방장아이디가있으면 팔로우한상태, 아니면 언팔로우

    public String getBgImagePath() {
        return profileImage;
    }

    public String getUserImagePath() {
        return accumWatchCnt;
    }

    public boolean isFollow() {
        // 방장아이디가 있으면 팔로우한 상태, 아니면 언팔로우
        if (TextUtils.isEmpty(accumWatchCnt) == false && accumWatchCnt.equals(castId)) {
            return true;
        }

        return false;
    }

    public String getUploadDate() {
        return castCode.replace(castId, "");
    }

    public String getTitle() {
        return castTitle;
    }

    public String getNickName() {
        return nickName;
    }

    public String getSubTitle() {
        return "";
    }

    public String getSubContent() {
        return "";
    }

    public int getTotalLikeCount() {
        if (TextUtils.isEmpty(likes)) {
            return 0;
        }

        return Integer.parseInt(likes);
    }

    public void addTotalLikeCount(int count) {
        int update = Integer.parseInt(likes) + count;
        likes = update + "";
    }

    public ContestData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(castId);
        dest.writeString(castCode);
        dest.writeInt(watchCnt);
        dest.writeString(castTitle);

        dest.writeInt(category);
        dest.writeInt(castPath);

        dest.writeString(nickName);
        dest.writeString(profileImage);
        dest.writeInt(castType);

        dest.writeString(castStartDate);
        dest.writeString(castEndDate);
        dest.writeInt(episode);
        dest.writeString(logoImage);
        dest.writeString(sortBig);
        dest.writeLong(sortMid);
        dest.writeString(sortSmall);
        dest.writeString(location);
        dest.writeString(store);
        dest.writeString(product);
        dest.writeString(likes);
        dest.writeString(accumWatchCnt);

    }

    protected ContestData(Parcel in) {
        castId = in.readString();
        castCode = in.readString();
        watchCnt = in.readInt();
        castTitle = in.readString();

        category = in.readInt();
        castPath = in.readInt();

        nickName = in.readString();
        profileImage = in.readString();
        castType = in.readInt();

        castStartDate = in.readString();
        castEndDate = in.readString();
        episode = in.readInt();
        logoImage = in.readString();
        sortBig = in.readString();
        sortMid = in.readLong();
        sortSmall = in.readString();
        location = in.readString();
        store = in.readString();
        product = in.readString();
        likes = in.readString();
        accumWatchCnt = in.readString();

    }

    public static final Creator<ContestData> CREATOR = new Creator<ContestData>() {
        @Override
        public ContestData createFromParcel(Parcel in) {
            return new ContestData(in);
        }

        @Override
        public ContestData[] newArray(int size) {
            return new ContestData[size];
        }
    };
}
