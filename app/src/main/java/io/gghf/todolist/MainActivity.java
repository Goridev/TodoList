package io.gghf.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import io.gghf.todolist.databinding.ActivityMainBinding;
import io.gghf.todolist.models.TaskAdapter;
import io.gghf.todolist.models.TaskLiveData;
import io.gghf.todolist.views.FragmentDialogCreate;
import io.gghf.todolist.views.RecyclerMainAdapter;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController controller;

    private FragmentManager manager;

    private TaskLiveData taskLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(controller.getGraph()).build();

        manager = getSupportFragmentManager();

        taskLiveData = new ViewModelProvider(this).get(TaskLiveData.class);

        binding.fabAdd.setOnClickListener(click -> {
            FragmentDialogCreate fragmentDialogCreate = FragmentDialogCreate.newInstance();
            fragmentDialogCreate.show(manager,"create_bottom_sheet");
        });
        binding.fabTrash.setOnClickListener(click -> {
                Log.d("FAB","trash");
                ArrayList<TaskAdapter> list = taskLiveData.getTasks().getValue();
                for(TaskAdapter item: list){
                    if(item.isSelected){
                        taskLiveData.removeTasks(item.task.text);
                    }
                }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}