package com.myapp.model;

public class SwipeSuggestion {
    String suggestion;
    String url;
    String description;

    public  SwipeSuggestion(String suggestion, String url, String description){
        this.suggestion = suggestion;
        this.url = url;
        this.description = description;
    }

    public  String getSuggestion(){
        return  suggestion;
    }

    public  void setSuggestion(){
        this.suggestion = suggestion;
    }

    public  String getUrl(){
        return url;
    }

    public void setUrl(){
        this.url = url;
    }

    public  String getDescription(){
        return description;
    }

    public void setDescription(){
        this.description = description;
    }
}
