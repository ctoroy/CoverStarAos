package com.shinleeholdings.coverstar.data;

import com.shinleeholdings.coverstar.util.CommentHelper;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.LoginHelper;

import java.util.ArrayList;
import java.util.Map;

public class CommentBase {
    public String id;

    public String contestUserId;
    public String userId;
    public String userImagePath;
    public String userNickName;

    public ArrayList<String> likes;
    public ArrayList<String> unLikes;
    public ArrayList<String> reports;

    public String message;
    public String messageDate;

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

    public CommentBase() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatItem.class)
    }

    public void setDefaultInfo(Map<String, Object> data) {
        try {
            userId = (String) data.get(CommentHelper.FIELDNAME_USERID);
            contestUserId = (String) data.get(CommentHelper.FIELDNAME_CONTESTUSERID);
            userImagePath = (String) data.get(CommentHelper.FIELDNAME_USERIMAGEPATH);
            userNickName = (String) data.get(CommentHelper.FIELDNAME_USERNICKNAME);
            messageDate = (String) data.get(CommentHelper.FIELDNAME_MESSAGE_DATE);
            message = (String) data.get(CommentHelper.FIELDNAME_MESSAGE);

            if (data.containsKey(CommentHelper.FIELDNAME_LIKES)) {
                likes = (ArrayList<String>) data.get(CommentHelper.FIELDNAME_LIKES);
            }

            if (data.containsKey(CommentHelper.FIELDNAME_UNLIKES)) {
                unLikes = (ArrayList<String>) data.get(CommentHelper.FIELDNAME_UNLIKES);
            }

            if (data.containsKey(CommentHelper.FIELDNAME_REPORTS)) {
                reports = (ArrayList<String>) data.get(CommentHelper.FIELDNAME_REPORTS);
            }
        } catch (Exception e) {
            DebugLogger.exception(e);
        }
    }
}
