package com.myapp.model;

public class ChatMessage {

    String user;
    String robot;
    int _id;

    public ChatMessage(String user, String robot){
        this.user = user;
        this.robot = robot;
    }

    // constructor
    public ChatMessage(int _id,String user, String robot){
        this.user = user;
        this.robot = robot;
        this._id = _id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRobot() {
        return robot;
    }

    public void setRobot(String robot) {
        this.robot = robot;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}