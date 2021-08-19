package io.gghf.todolist;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import io.gghf.todolist.databinding.ActivityMainBinding;
import io.gghf.todolist.models.TaskAdapter;
import io.gghf.todolist.models.TaskLiveData;
import io.gghf.todolist.views.FragmentDialogBiometric;
import io.gghf.todolist.views.FragmentDialogCreate;
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
        FragmentDialogBiometric fragmentDialogBiometric = FragmentDialogBiometric.newInstance();
        fragmentDialogBiometric.setCancelable(false);
        fragmentDialogBiometric.show(manager,"fingerprintFragment");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}