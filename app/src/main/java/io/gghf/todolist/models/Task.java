package io.gghf.todolist.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
/*
*
*
*
* */
public class Task implements Parcelable {
    public String text;
    public String title;
    public Timestamp createdDate;
    public String state;

    protected Task(Parcel in){
        title = in.readString();
        text = in.readString();
        createdDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        state = in.readString();
    }
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Task>(){
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Task createFromParcel(Parcel parcel) {
            return new Task(parcel);
        }

        @Override
        public Task[] newArray(int i) {
            return new Task[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeParcelable(createdDate,PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeString(state);
    }
}
