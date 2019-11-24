package com.myapp.model;


public class HelpDeskModel {

    private String name;
    private int photo;
    private int background;

    public HelpDeskModel(String name, int photo,int background) {
        this.name = name;
        this.photo = photo;
        this.background = background;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
