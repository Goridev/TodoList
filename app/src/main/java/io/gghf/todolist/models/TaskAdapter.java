package io.gghf.todolist.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class TaskAdapter extends Task implements Parcelable {
    public Task task;
    public boolean isSelected = false;
    public boolean showAllCheckBox = false;
    public TaskAdapter(Task task){
        this.task = task;
    }
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<TaskAdapter>(){
        @Override
        public TaskAdapter createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public TaskAdapter[] newArray(int i) {
            return new TaskAdapter[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(task);
        parcel.writeBoolean(isSelected);
        parcel.writeBoolean(showAllCheckBox);
    }
}
