package io.gghf.todolist.models;

import com.google.firebase.Timestamp;

import java.util.Date;

/*
*
*
*
* */
public class Task {
    public String title;
    public String text;
    public Timestamp createdDate;
    public String state;

    public Task(){ }
    public Task(String title,String text,Timestamp createdDate,String state){
        this.title = title;
        this.text = text;
        this.createdDate = createdDate;
        this.state = state;
    }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state;}
}
