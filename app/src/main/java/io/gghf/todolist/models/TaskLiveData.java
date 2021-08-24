package io.gghf.todolist.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.gghf.todolist.views.RecyclerMainAdapter;

public class TaskLiveData extends ViewModel {
    private FirebaseFirestore db;
    private CollectionReference ref;
    private ArrayList<Task> tasks;
    private ArrayList<TaskAdapter> tasksAdapter = new ArrayList<>();
    private MutableLiveData<ArrayList<TaskAdapter>> tasksAdapterLive;
    public LiveData<ArrayList<TaskAdapter>> getTasks(){
        if(tasksAdapterLive == null){
            tasksAdapterLive = new MutableLiveData<ArrayList<TaskAdapter>>();
            fetchDBTasks();
        }
        return tasksAdapterLive;
    }
    public void updateTasks(int position){
        if(tasksAdapterLive.getValue().get(position).isSelected){
            tasksAdapterLive.getValue().get(position).isSelected = false;
        }else{
            tasksAdapterLive.getValue().get(position).isSelected = true;
        }
    }
    public void removeTasks(String text){
        db = FirebaseFirestore.getInstance();
        ref = db.collection("TodoList");
        ref.whereEqualTo("text",text).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot: task.getResult()){
                        ref.document(snapshot.getId()).delete();
                    }
                }else{
                    Log.e("FIREBASE","Error to get document with this title",task.getException());
                }
            }
        });
    }

    public void updateDBTask(String equalTo,String fieldPath, Object value){
        db = FirebaseFirestore.getInstance();
        ref = db.collection("TodoList");
        ref.whereEqualTo("text",equalTo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot: task.getResult()){
                        ref.document(snapshot.getId()).update(fieldPath,value).addOnSuccessListener(item -> {
                            Log.d("FIREBASE","Update Success");
                        }).addOnFailureListener(failure -> {
                            Log.e("FIREBASE",failure.getMessage());
                        });
                    }
                }else{
                    Log.e("FIREBASE","Error to get document with this title",task.getException());
                }
            }
        });
    }
    public String convertTimestamp(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(timestamp.toDate());
    }
    private void fetchDBTasks(){
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        ref = db.collection("TodoList");
        ref.orderBy("createdDate", Query.Direction.ASCENDING).addSnapshotListener(MetadataChanges.INCLUDE,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                tasksAdapter.clear();
                if (error != null) {
                    Log.d("FIREBASE", "Listen failed.", error);
                    return;
                }
                for(QueryDocumentSnapshot doc: value){
                    tasksAdapter.add(new TaskAdapter(doc.toObject(Task.class)));
                }
                tasksAdapterLive.setValue(tasksAdapter);
            }
        });
    }
}
