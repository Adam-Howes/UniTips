package com.example.unitips.ViewContent;

public class Comment {
    private String commentDescription;
    private String commentUserName;
    private String commentPostDate;

    public Comment() {/* Empty Constructor*/}

    public Comment(String commentDescription, String commentPostDate, String commentUserName) {
        this.commentDescription = commentDescription;
        this.commentPostDate = commentPostDate;
        this.commentUserName = commentUserName;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public String getCommentPostDate() {
        return commentPostDate;
    }
}
