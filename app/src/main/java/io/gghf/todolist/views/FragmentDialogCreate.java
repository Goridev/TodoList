package io.gghf.todolist.views;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import io.gghf.todolist.R;
import io.gghf.todolist.models.Task;

public class FragmentDialogCreate extends BottomSheetDialogFragment implements View.OnClickListener {

    private View root;
    private TextView title;
    private TextView text;
    private Button btn;
    private FirebaseFirestore db;
    private CollectionReference ref;

    public static FragmentDialogCreate newInstance() {
        Bundle args = new Bundle();
        FragmentDialogCreate fragment = new FragmentDialogCreate();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = height-400;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,R.style.BottomSheetDialogStyle);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dialog_create,container,false);
        btn = root.findViewById(R.id.button_main_create);
        title = root.findViewById(R.id.edit_main_create_title);
        text = root.findViewById(R.id.edit_main_create_text);

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_main_create:
                db = FirebaseFirestore.getInstance();
                ref = db.collection("TodoList");
                Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime());
                int titleLength = title.getText().toString().length();
                int textLength = text.getText().toString().length();
                ref.add(new Task(
                        title.getText().toString().substring(0,1).toUpperCase()+title.getText().toString().substring(1,titleLength),
                        text.getText().toString().substring(0,1).toUpperCase()+text.getText().toString().substring(1,textLength),
                        timestamp,"Todo")).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dismiss();
                    }
                });
                break;
        }
    }
}
