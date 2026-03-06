package com.example.exercice_5_a_10.exo8;

public class Article {

    private String title;
    private String id;

    public Article() {
    }

    public Article(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}