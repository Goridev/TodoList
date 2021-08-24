package io.gghf.todolist.views;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;

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

public class FragmentDialogCreate extends BottomSheetDialogFragment implements View.OnClickListener, FragmentResultListener {

    private View root;
    private TextView title;
    private TextView text;
    private Button btn;
    private FirebaseFirestore db;
    private CollectionReference ref;

    public FragmentDialogCreate.ContextMenuAdapter adapter;

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
        getChildFragmentManager().setFragmentResultListener("requestInputBoxText", this,this);
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
        text.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { setPopupMenu(text);return false;}
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { actionMode = null;return false; }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_main_create:
                getSpanStyleIndex(text.getEditableText());
                if(text.getText().length() > 0 && title.getText().length() > 0){
                    db = FirebaseFirestore.getInstance();
                    ref = db.collection("TodoList");
                    Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime());
                    int titleLength = title.getText().toString().length();
                    int textLength = text.getText().toString().length();
                    String titleFinal = title.getText().toString();
                    String textFinal = HtmlCompat.toHtml((Spanned) text.getText(),HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                    ref.add(new Task(titleFinal, textFinal, timestamp,"Todo")).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            dismiss();
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Min (1) champs de texte vide...", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void setPopupMenu(View v){
        adapter = new FragmentDialogCreate.ContextMenuAdapter(getSelectedText(),getStartIndex(),getEndIndex());
        ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAnchorView(v);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setModal(true);
        listPopupWindow.show();
    }
    public void getSpanStyleIndex(Editable e) {
        StyleSpan[] ss = e.getSpans(0,e.length(),StyleSpan.class);
        for(StyleSpan span : ss){
            int start = e.getSpanStart(span);
            int end = e.getSpanEnd(span);
            Log.d("SPAN", "style on index -> "+start+" to "+ end);
        }
    }
    public int getStartIndex(){
        int min = 0;
        if (text.isFocused()) {
            final int selStart = text.getSelectionStart();
            final int selEnd = text.getSelectionEnd();
            min = Math.max(0, Math.min(selStart, selEnd));
        }
        return min;
    }
    public int getEndIndex(){
        int max = text.getText().length();
        if (text.isFocused()) {
            final int selStart = text.getSelectionStart();
            final int selEnd = text.getSelectionEnd();
            max = Math.max(0, Math.max(selStart, selEnd));
        }
        return max;
    }
    public CharSequence getSelectedText(){
        return text.getText().subSequence(getStartIndex(), getEndIndex());
    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        Log.d("FragmentDialogCreate -> FragmentResultListener",requestKey);
        adapter.setSpanText(new URLSpan(result.getString("url")));
    }

    private final class ContextMenuAdapter extends BaseAdapter {

        private View root;
        private ImageView bold;
        private ImageView italic;
        private ImageView underline;
        private ImageView strike;
        private ImageView increase;
        private ImageView decrease;
        private ImageView link;
        private ImageView listBulleted;
        private ImageView listNumbered;

        private ConstraintLayout context_menu_edit;

        private CharSequence selectedText;
        private int startIndex;
        private int endIndex;

        private float increaseSize = 1.5f;
        private int increaseCount = 0;

        private float decreaseSize = 0.5f;

        public ContextMenuAdapter(CharSequence selectedText,int startIndex,int endIndex) {
            this.selectedText = selectedText;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public int getCount() { return 1; }
        @Override
        public Object getItem(int position) { return null; }
        @Override
        public long getItemId(int position) { return 0; }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            root = inflater.inflate(R.layout.context_menu_edit,parent, false);
            bold = root.findViewById(R.id.bold);
            italic = root.findViewById(R.id.italic);
            underline = root.findViewById(R.id.underline);
            strike = root.findViewById(R.id.strike);
            increase = root.findViewById(R.id.increase_text);
            decrease = root.findViewById(R.id.decrease_text);
            link = root.findViewById(R.id.link_text);
            listBulleted = root.findViewById(R.id.list_bulleted_text);
            listNumbered = root.findViewById(R.id.list_numbered_text);


            context_menu_edit = root.findViewById(R.id.context_menu_edit);
            bold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSpanText(new StyleSpan(Typeface.BOLD));
                }
            });
            italic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSpanText(new StyleSpan(Typeface.ITALIC));
                }
            });
            underline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSpanText(new UnderlineSpan());
                }
            });
            strike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSpanText(new StrikethroughSpan());
                }
            });
            increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(increaseCount > 0 ){
                        increaseSize = increaseSize+(increaseCount/2);
                    }
                    increaseCount++;

                    setSpanText(new RelativeSizeSpan(increaseSize));
                }
            });
            decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    increaseCount = 0;
                    increaseSize = 1f;
                    setSpanText(new RelativeSizeSpan(decreaseSize));
                }
            });
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContextMenuInputBox contextMenuInputBox = ContextMenuInputBox.newInstance();
                    contextMenuInputBox.show(getChildFragmentManager(),"ContextMenuInputBox");
                }
            });
            listBulleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            listNumbered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            return root;
        }
        public void setSpanText(Object what){
            SpannableString leftSpan = new SpannableString(text.getText().subSequence(0,startIndex));
            SpannableString rightSpan = new SpannableString(text.getText().subSequence(endIndex,text.getText().length()));
            SpannableString selectedSpan = new SpannableString(selectedText);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Log.d("FragmentDialogCreatePopupMenu","LEFT -> "+ text.getText().subSequence(0,startIndex));
            Log.d("FragmentDialogCreatePopupMenu","SELECTED -> "+ selectedText);
            Log.d("FragmentDialogCreatePopupMenu","RIGHT -> "+ text.getText().subSequence(endIndex,text.getText().length()));
            selectedSpan.setSpan(what,0,selectedSpan.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(leftSpan);
            builder.append(selectedSpan);
            builder.append(rightSpan);
            text.setText(builder);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

}
