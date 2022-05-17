package com.shinleeholdings.coverstar.data;

import com.shinleeholdings.coverstar.util.LoginHelper;

import java.util.ArrayList;

public class CommentItem {
//    답글 아이디 : 자동생성??
//    경연 업로드 사용자 아이디 : 8201042423131
//    댓글단 사용자 아이디 : 8201031240677
//    댓글단 사용자 썸네일 이미지 : 이미지경로
//    댓글단 사용자 닉네임 : 헤헤
//    댓글 작성 날짜 : yyyymmddhhmmss
//    댓글 내용 : 커버스타 짱이에요!!!
//    좋아요 수 : 200
//    싫어요 수 : 100
//    답글 수 : 50

    public String id;
    public String contestUserId;
    public String userId;
    public String userImagePath;
    public String userNickName;
    public String commentDate;
    public String comment;
    public ArrayList<String> likes;
    public ArrayList<String> unLikes;
    public ArrayList<String> comments;
    public ArrayList<String> reports;

    public int getLikeCount() {
        if (likes == null) {
            likes = new ArrayList<>();
        }

        return likes.size();
    }

    public int getUnLikeCount() {
        if (unLikes == null) {
            unLikes = new ArrayList<>();
        }

        return unLikes.size();
    }

    public int getCommentCount() {
        if (comments == null) {
            comments = new ArrayList<>();
        }

        return comments.size();
    }

    public boolean isMyContestComment() {
        return contestUserId.equals(LoginHelper.getSingleInstance().getLoginUserId());
    }

    public boolean isMyComment() {
        return userId.equals(LoginHelper.getSingleInstance().getLoginUserId());
    }

    public boolean alreadyLike() {
        if (likes == null) {
            likes = new ArrayList<>();
            return false;
        }
        return likes.contains(LoginHelper.getSingleInstance().getLoginUserId());
    }

    public boolean alreadyUnLike() {
        if (unLikes == null) {
            unLikes = new ArrayList<>();
            return false;
        }
        return unLikes.contains(LoginHelper.getSingleInstance().getLoginUserId());
    }

    public void addLike() {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        likes.add(LoginHelper.getSingleInstance().getLoginUserId());
    }

    public void removeLike() {
        if (likes != null) {
            likes.remove(LoginHelper.getSingleInstance().getLoginUserId());
        } else {
            likes = new ArrayList<>();
        }
    }

    public void addUnLike() {
        if (unLikes == null) {
            unLikes = new ArrayList<>();
        }
        unLikes.add(LoginHelper.getSingleInstance().getLoginUserId());
    }

    public void removeUnLike() {
        if (unLikes != null) {
            unLikes.remove(LoginHelper.getSingleInstance().getLoginUserId());
        } else {
            unLikes = new ArrayList<>();
        }
    }

    public boolean addReport() {
        if (reports != null) {
            if (reports.contains(LoginHelper.getSingleInstance().getLoginUserId())) {
                return false;
            }
        } else {
            reports = new ArrayList<>();
        }
        reports.add(LoginHelper.getSingleInstance().getLoginUserId());
        return true;
    }

    public CommentItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatItem.class)
    }
}
