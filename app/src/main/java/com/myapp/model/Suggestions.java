package com.myapp.model;


public class Suggestions {

    private String suggestionText;

    public Suggestions() {
    }

    public Suggestions(String suggestion) {
        this.suggestionText = suggestion;
    }

    public String getSuggestion() {
        return suggestionText;
    }

    public void setSuggestion(String suggestion) {
        this.suggestionText = suggestion;
    }

}
