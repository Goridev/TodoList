package io.gghf.todolist.views;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.gghf.todolist.R;
import io.gghf.todolist.models.TaskAdapter;
import io.gghf.todolist.models.TaskLiveData;

public class FragmentDialogDetailsCalendar extends DialogFragment implements View.OnClickListener, CalendarView.OnDateChangeListener {

    private static final int MY_CAL_WRITE_REQ = 2908;
    private View root;
    private CalendarView calendarView;
    private Button buttonSetCalendar;

    private TaskAdapter adapter;
    private TaskLiveData taskLiveData;

    private long savedDate;

    public static FragmentDialogDetailsCalendar newInstance(TaskAdapter adapter) {
        Bundle args = new Bundle();
        FragmentDialogDetailsCalendar fragment = new FragmentDialogDetailsCalendar();
        args.putParcelable("task",adapter);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskLiveData = new ViewModelProvider(getActivity()).get(TaskLiveData.class);
        try{
            adapter = getArguments().getParcelable("task");
        }catch (Exception e){
            dismiss();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dialog_details_calendar,container,false);
        calendarView = root.findViewById(R.id.reminder_calendar);
        buttonSetCalendar = root.findViewById(R.id.button_set_calendar);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);
        calendarView.setOnDateChangeListener(this);
        buttonSetCalendar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_set_calendar:
                addEvent();
                break;
            default:
                break;
        }
    }
    private void addEvent(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, MY_CAL_WRITE_REQ);
        }
        ContentResolver cr = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, savedDate);
        values.put(CalendarContract.Events.DTEND, savedDate);
        values.put(CalendarContract.Events.TITLE, adapter.task.title);
        values.put(CalendarContract.Events.DESCRIPTION, adapter.task.text);
        values.put(CalendarContract.Events.CALENDAR_ID, 2);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Paris");
        //values.put(CalendarContract.Events.EVENT_LOCATION, "Brussels");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getArguments().remove("task");
    }
    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
        Calendar time = Calendar.getInstance();
        time.set(year, month, day);
        savedDate = time.getTimeInMillis();
    }
}
