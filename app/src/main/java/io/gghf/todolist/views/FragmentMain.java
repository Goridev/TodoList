package io.gghf.todolist.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import io.gghf.todolist.R;
import io.gghf.todolist.models.Task;
import io.gghf.todolist.models.TaskAdapter;
import io.gghf.todolist.models.TaskLiveData;

public class FragmentMain extends Fragment {

    private View root;
    // Recycler
    private RecyclerView rv;
    private RecyclerView.Adapter rva;
    private RecyclerView.LayoutManager rvm;
    // Manager
    private FragmentManager manager;
    // Background
    private BackgroundTask async;
    private ArrayList<Task> tasks = new ArrayList<>();
    // LiveData
    private TaskLiveData taskLiveData;
    // External data
    private Intent fileIntent;

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
    ActivityResultLauncher<Intent> activityGetLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        intentActivityExportData(data.getData());
                    }
                }
            });
    ActivityResultLauncher<Intent> activityCreateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        new BackgroundWriteFile(getContext(),data.getData());
                    }
                }
            });

    public void intentActivityGetFolder() {
        fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        fileIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,"/");
        activityGetLauncher.launch(fileIntent);
    }
    public void intentActivityExportData(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        activityCreateLauncher.launch(intent);
    }

    private String generateJSON(ArrayList<TaskAdapter> tasks){
        JSONArray jsonArray = new JSONArray(tasks);
        return jsonArray.toString();
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
    private class BackgroundWriteFile extends AsyncTaskLoader<Void>{
        private Context ctx;
        private Uri uri;

        public BackgroundWriteFile(@NonNull Context context, Uri uri) {
            super(context);
            this.ctx = context;
            this.uri = uri;
        }
        @Nullable
        @Override
        public Void loadInBackground() {
            try {
                Log.d("FragmentMain","CREATE_FILE -> uri : "+uri);
                OutputStream fileOutputStream =  getActivity().getContentResolver().openOutputStream(uri);
                byte[] html = generateJSON(taskLiveData.getTasks().getValue()).getBytes();
                Log.d("FragmentMain","CREATE_FILE -> html generated -> "+html);
                fileOutputStream.flush();
                fileOutputStream.write(html);
                fileOutputStream.close();
                Log.d("FragmentMain","CREATE_FILE -> File updated "+uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}