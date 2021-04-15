package com.anujsingh.myapplication;

public class note {
    private String title;
    private String description;
    private int priority;

    public note() {
        //empty constructor needed
    }

    public note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
