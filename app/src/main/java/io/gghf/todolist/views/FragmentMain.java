package io.gghf.todolist.views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import io.gghf.todolist.R;
import io.gghf.todolist.models.Task;
import io.gghf.todolist.models.TaskLiveData;

public class FragmentMain extends Fragment {

    private View root;

    private RecyclerView rv;
    private RecyclerView.Adapter rva;
    private RecyclerView.LayoutManager rvm;

    private FragmentManager manager;

    private BackgroundTask async;
    private ArrayList<Task> tasks = new ArrayList<>();

    private TaskLiveData taskLiveData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskLiveData = new ViewModelProvider(requireActivity()).get(TaskLiveData.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        rv = root.findViewById(R.id.task_recycler);
        rv.setHasFixedSize(true);
        rvm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(rvm);
        async = new BackgroundTask(getContext(),manager);
        async.loadInBackground();
        return root;
    }

    private class BackgroundTask extends AsyncTaskLoader<Void>{
        private FragmentManager manager;
        public BackgroundTask(Context ctx,FragmentManager manager){
            super(ctx);
            this.manager = manager;
        }
        @Nullable
        @Override
        public Void loadInBackground() {
            try{
                taskLiveData.getTasks().observe(getViewLifecycleOwner(),item -> {
                    rva = new RecyclerMainAdapter(item,getContext(),getActivity(),manager);
                    rv.scrollToPosition(rva.getItemCount()>0?rva.getItemCount()-1:rva.getItemCount());
                    rv.smoothScrollToPosition(rva.getItemCount()>0?rva.getItemCount()-1:rva.getItemCount());
                    rva.notifyDataSetChanged();
                    rv.setAdapter(rva);
                });
            }catch (Exception e){
                Log.e("Firebase", "Exception ",e);
            }
            return null;
        }
    }
}