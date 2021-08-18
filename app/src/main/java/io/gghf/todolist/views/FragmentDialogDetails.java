package io.gghf.todolist.views;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import io.gghf.todolist.R;
import io.gghf.todolist.models.TaskAdapter;
import io.gghf.todolist.models.TaskLiveData;

public class FragmentDialogDetails extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private TaskAdapter adapter;
    private TaskLiveData taskLiveData;

    private View root;

    private TextView title;
    private TextView text;
    private TextView created_date;
    private Spinner state;

    private ArrayAdapter<CharSequence> arrayAdapter;

    public static FragmentDialogDetails newInstance(TaskAdapter task) {
        Bundle args = new Bundle();
        args.putParcelable("task", (Parcelable) task);
        FragmentDialogDetails fragment = new FragmentDialogDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
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
        root = inflater.inflate(R.layout.fragment_dialog_details,container,false);
        title = root.findViewById(R.id.details_title_task);
        text = root.findViewById(R.id.details_text_task);
        created_date = root.findViewById(R.id.details_created_date_task);
        state = root.findViewById(R.id.details_state_task);
        arrayAdapter = ArrayAdapter.createFromResource(getContext(),R.array.state_task,R.layout.fragment_dialog_details_spinner_state);
        arrayAdapter.setDropDownViewResource(R.layout.fragment_dialog_details_spinner_state);
        state.setAdapter(arrayAdapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(adapter.task.title);
        text.setText(adapter.task.text);
        created_date.setText(taskLiveData.convertTimestamp(adapter.task.createdDate));
        state.setOnItemSelectedListener(this);
        if(adapter.task.state.length() > 0){
            state.setSelection(arrayAdapter.getPosition(adapter.task.state),true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        taskLiveData.updateDBTask(adapter.task.text,"state",adapterView.getItemAtPosition(i).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if(adapter.task.state.length() > 0){
            state.setSelection(arrayAdapter.getPosition(adapter.task.state),true);
        }
    }
}
