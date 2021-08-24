package io.gghf.todolist.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.gghf.todolist.R;
import io.gghf.todolist.models.Task;
import io.gghf.todolist.models.TaskAdapter;
import io.gghf.todolist.models.TaskLiveData;

public class RecyclerMainAdapter extends RecyclerView.Adapter<RecyclerMainAdapter.RecyclerMainViewHolder>{

    private FragmentManager manager;
    private FragmentActivity activity;
    private Context ctx;
    private View root;

    public ArrayList<TaskAdapter> binding = new ArrayList<>();
    private TaskLiveData taskLiveData;

    public static class RecyclerMainViewHolder extends RecyclerView.ViewHolder{
        public CardView container;
        public ConstraintLayout boxData;
        public TextView titleTask;
        public TextView textTask;
        public TextView createdDateTask;
        public CheckBox checkBox;
        public TextView stateTask;
        public RecyclerMainViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.cardview_task_recycler);
            boxData = itemView.findViewById(R.id.constraint_task_recycler);
            titleTask = itemView.findViewById(R.id.title_task_recycler);
            textTask = itemView.findViewById(R.id.text_task_recycler);
            createdDateTask = itemView.findViewById(R.id.created_date_task_recycler);
            checkBox = itemView.findViewById(R.id.checkbox_recycler);
            stateTask = itemView.findViewById(R.id.state_task_recycler);
        }
    }
    public RecyclerMainAdapter(ArrayList<TaskAdapter> tasksAdpter, Context ctx, FragmentActivity activity,FragmentManager manager){
        this.ctx = ctx;
        this.activity = activity;
        this.binding = tasksAdpter;
        this.manager = manager;
        taskLiveData = new ViewModelProvider(activity).get(TaskLiveData.class);
    }
    @NonNull
    @Override
    public RecyclerMainAdapter.RecyclerMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_recycler,parent,false);
        RecyclerMainViewHolder holder = new RecyclerMainViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerMainAdapter.RecyclerMainViewHolder holder,int position) {
        try{
            Spanned span = HtmlCompat.fromHtml(binding.get(position).task.getText(),HtmlCompat.FROM_HTML_MODE_LEGACY);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date jdate = binding.get(position).task.getCreatedDate().toDate();
            holder.titleTask.setText(formattingTitle(binding.get(position).task.getTitle()));
            holder.textTask.setText(formattingText(span));
            holder.createdDateTask.setText(sdf.format(jdate));
            holder.stateTask.setText(binding.get(position).task.getState());
            holder.container.setOnClickListener(click -> {
                    Log.d("RecyclerMain","[onClick] Container");
                    //setSelectedItem(holder.getAdapterPosition())
                    if(holder.checkBox.getVisibility() == View.VISIBLE){
                        taskLiveData.updateTasks(holder.getAdapterPosition());
                        if(holder.checkBox.isChecked()){
                            holder.checkBox.setChecked(false);
                        }else{
                            holder.checkBox.setChecked(true);
                        }
                    }else{
                        FragmentDialogDetails details = FragmentDialogDetails.newInstance(binding.get(position));
                        details.show(activity.getSupportFragmentManager(),"dialog_details");
                    }
            });
            holder.container.setOnLongClickListener(click -> {
                Log.d("RecyclerMain","[onLongClick] checkbox visible ? "+holder.checkBox.getVisibility());
                setToogleAllCheckBox();
                return false;
            });

            if(binding.get(position).showAllCheckBox){
                setCheckBoxAnimated(holder.container,holder.boxData,holder.titleTask,holder.textTask,holder.checkBox,true,15,25);
            }else{
                setCheckBoxAnimated(holder.container,holder.boxData,holder.titleTask,holder.textTask,holder.checkBox,false,1,25);
            }
        }catch (Exception e){
            Log.d("RecyclerMain","[Error]",e);
        }
    }
    @Override
    public int getItemCount() { return binding.size(); }
    public void setSelectedItem(int position){
        if(binding.get(position).isSelected){
            binding.get(position).isSelected = true;

        }else{
            binding.get(position).isSelected = false;
        }
    }
    public void setToogleAllCheckBox(){
        for(TaskAdapter item: binding){
            if(item.showAllCheckBox){
                item.showAllCheckBox = false;
            }else{
                item.showAllCheckBox = true;

            }
        }
        notifyDataSetChanged();
    }
    public String formattingTitle(String title){
        String reducer = title;
        if(title.length() > 30){
            reducer = title.substring(0,1).toUpperCase()+title.substring(1,27)+"...";
        }
        return reducer;
    }
    public String formattingText(Spanned text){
        String reducer = text.toString();
        if(text.length() > 50){
            reducer = text.toString().substring(0,1).toUpperCase()+text.toString().substring(1,50)+"...";
        }
        return reducer;
    }
    public void setCheckBoxAnimated(CardView root,ConstraintLayout box,TextView title,TextView text,CheckBox checkBox,boolean visible,int padding_margin_left_in_dp,int padding_bottom_in_dp){
        final float scale_margin = ctx.getResources().getDisplayMetrics().density;
        int padding_margin_left_in_px = (int) (padding_margin_left_in_dp * scale_margin + 0.5f);

        final float scale_padding = ctx.getResources().getDisplayMetrics().density;
        int padding_bottom_in_px = (int) (padding_bottom_in_dp * scale_padding + 0.5f);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) box.getLayoutParams();
                params.leftMargin = (int) (padding_margin_left_in_px * interpolatedTime);
                title.setPadding(padding_margin_left_in_px,0,0,padding_bottom_in_px);
                text.setPadding(padding_margin_left_in_px,0,0,0);
                box.setLayoutParams(params);
                FloatingActionButton fab_add = activity.findViewById(R.id.fab_add);
                FloatingActionButton fab_trash = activity.findViewById(R.id.fab_trash);
                if(visible){
                    checkBox.setVisibility(View.VISIBLE);
                    fab_add.setVisibility(View.INVISIBLE);
                    fab_trash.setVisibility(View.VISIBLE);
                }else{
                    checkBox.setVisibility(View.INVISIBLE);
                    fab_trash.setVisibility(View.INVISIBLE);
                    fab_add.setVisibility(View.VISIBLE);
                }
            }
        };
        animation.setDuration(500);
        root.startAnimation(animation);
    }
}
