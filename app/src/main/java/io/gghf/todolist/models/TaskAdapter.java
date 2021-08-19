package io.gghf.todolist.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Map;

public class TaskAdapter extends Task implements Parcelable {
    public Task task;
    public boolean isSelected = false;
    public boolean showAllCheckBox = false;
    public TaskAdapter(Task task){
        this.task = task;
    }

    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<TaskAdapter>(){
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public TaskAdapter createFromParcel(Parcel parcel) {
            return new TaskAdapter(parcel);
        }

        @Override
        public TaskAdapter[] newArray(int i) {
            return new TaskAdapter[i];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected TaskAdapter(Parcel in){
        task = (Task) in.readValue(Task.class.getClassLoader());
        isSelected = in.readBoolean();
        showAllCheckBox = in.readBoolean();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(task,PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeBoolean(isSelected);
        parcel.writeBoolean(showAllCheckBox);
    }
}
