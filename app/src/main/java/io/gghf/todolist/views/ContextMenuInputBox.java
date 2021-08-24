package io.gghf.todolist.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import io.gghf.todolist.R;

public class ContextMenuInputBox extends DialogFragment implements View.OnClickListener {

    private View root;
    private EditText edit;
    private Button button;

    public static ContextMenuInputBox newInstance() {
        Bundle args = new Bundle();
        ContextMenuInputBox fragment = new ContextMenuInputBox();
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

        int width = displayMetrics.widthPixels;

        if (dialog != null) {
            View context_menu_edit = dialog.findViewById(R.id.context_menu_edit);
            context_menu_edit.getLayoutParams().width = width;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.context_menu_edit_input_box,container,false);
        edit = root.findViewById(R.id.edit_link_text);
        button = root.findViewById(R.id.button_submit_edit_link);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_submit_edit_link:
                Bundle bundle = new Bundle();
                bundle.putString("url",edit.getText().toString());
                getParentFragmentManager().setFragmentResult("requestInputBoxText",bundle);
                dismiss();
                break;
            default:
                break;
        }
    }
}
