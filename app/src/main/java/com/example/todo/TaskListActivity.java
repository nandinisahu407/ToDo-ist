package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.Adapter.SelectedDayAdapter;
import com.example.todo.Adapter.ToDoAdapter;
import com.example.todo.Model.ToDoModel;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskListActivity extends AppCompatActivity {

    TextView date;
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    SelectedDayAdapter adapter1;
    SelectedDayAdapter adapter2;
    ArrayList<ToDoModel> taskList1;
    private ArrayList<ToDoModel> taskList2;
    private DatabaseReference rootRef;
    private DatabaseReference tasksRef;
    private DatabaseReference dateRef;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        date=findViewById(R.id.date_of_tasks);


        String selectedDateStr = getIntent().getStringExtra("selectedDate");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        Calendar selectedDate = Calendar.getInstance();
        try {
            selectedDate.setTime(dateFormat.parse(selectedDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));
        tasksRef = rootRef.child("tasks");

        String selectedDateKey = dateFormat.format(selectedDate.getTime());

        dateRef = tasksRef.child(selectedDateKey);

//        System.out.println("Date in task");
//        System.out.println(selectedDateKey);

        date.setText(selectedDateKey);

        recyclerView1 = findViewById(R.id.doneTasks_RecyclerView);
        recyclerView1.setHasFixedSize(false);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2= findViewById(R.id.notdonetasks_RecyclerView);
        recyclerView2.setHasFixedSize(false);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        taskList1 = new ArrayList<>();
        taskList2 = new ArrayList<>();

        adapter1 = new SelectedDayAdapter(taskList1,TaskListActivity.this);
        recyclerView1.setAdapter(adapter1);

        adapter2= new SelectedDayAdapter(taskList2,TaskListActivity.this);
        recyclerView2.setAdapter(adapter2);

        dateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList1.clear();
                taskList2.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        if(dataSnapshot.hasChild("task")){
                            ToDoModel t= dataSnapshot.getValue(ToDoModel.class);
                            if (t.getStatus()==0) {   //not completed
                                taskList2.add(t);
                            } else if (t.getStatus()==1) {         //all completed tasks
                                taskList1.add(t);
                            }
                        }
                    }

                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Query the Firebase Realtime Database for tasks with the selected date


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), Profile_user.class));
                    break;

                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    break;

            }
            return true;
        });
    }
}
