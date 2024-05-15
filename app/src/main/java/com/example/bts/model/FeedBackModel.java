package com.example.bts.model;

public class FeedBackModel {
    private String userId;
    private String feedbackText;
    private long timestamp;
    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setFeedbackText(String feedbackText){
        this.feedbackText = feedbackText;
    }
    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }
    public String getUserId(){
        return userId;
    }
    public String getFeedbackText(){
        return feedbackText;
    }
    public long getTimestamp(){
        return timestamp;
    }
    public String toString(){
        return "userId: " + userId + ", feedbackText: " + feedbackText + ", timestamp: " + timestamp;
    }
}

