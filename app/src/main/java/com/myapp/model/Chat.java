package com.myapp.model;


public class Chat {
    String sentMsg;
    String time;
    Boolean isSent;
    Boolean isImage;


    public Chat(String sentMsg,String time,Boolean isSent,Boolean isImage){
        this.sentMsg = sentMsg;
        this.time = time;
        this.isSent = isSent;
        this.isImage= isImage;

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getSent() {
        return isSent;
    }

    public void setSent(Boolean sent) {
        isSent = sent;
    }

    public String getSentMsg() {
        return sentMsg;
    }

    public void setSentMsg(String sentMsg) {
        this.sentMsg = sentMsg;
    }

    public Boolean isSent() {
        return isSent;
    }

    public Boolean isImage() {
        return isImage;
    }


}
