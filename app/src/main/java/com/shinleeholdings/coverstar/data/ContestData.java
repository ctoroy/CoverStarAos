package com.shinleeholdings.coverstar.data;

import com.google.gson.annotations.SerializedName;

public class ContestData {
    //+:경연 *:이벤트
//	public String castCode; //+*방키 방장아이디+yyyymmddhhmmss ctoroy20210726162622
//	public String castId; //+*방장 아이디
//	public Integer watchCnt; //플레이수
//	public String castTitle; //+*타이틀
//	public Integer category;
//	public Integer castPath;
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
    @SerializedName("castCode") public String castCode; //+*방키 방장아이디+yyyymmddhhmmss ctoroy20210726162622
    @SerializedName("castId") public String castId;  //+*방장 아이디
    @SerializedName("watchCnt") public int watchCnt; //플레이수
    @SerializedName("castTitle") public String castTitle; //+*타이틀

    @SerializedName("category") public String category;
    @SerializedName("castPath") public String castPath;

    @SerializedName("nickName") public String nickName; //+*참가자명
    @SerializedName("profileImage") public String profileImage; //+*썸네일 이미지
    @SerializedName("castType") public int castType; //0:+,1:*

    @SerializedName("castStartDate") public String castStartDate; //+*시작시간
    @SerializedName("castEndDate") public String castEndDate; //+*종료시간
    @SerializedName("episode") public int episode; //+*댓글수
    @SerializedName("logoImage") public String logoImage; //+*원곡가수명
    @SerializedName("sortBig") public String sortBig; //+*한마디
    @SerializedName("sortMid") public String sortMid; //*이벤트총상금
    @SerializedName("sortSmall") public String sortSmall; //*이벤트타이틀
    @SerializedName("location") public String location; //영상 URL
    @SerializedName("store") public String store; //*이벤트발표일
    @SerializedName("product") public String product; //*이벤트 이미지
    @SerializedName("likes") public String likes;
    @SerializedName("accumWatchCnt") public String accumWatchCnt;




}
