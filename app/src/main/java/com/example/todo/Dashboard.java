package com.example.todo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.todo.Adapter.ToDoAdapter;
import com.example.todo.Model.ToDoModel;
import com.example.todo.Utils.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Dashboard extends AppCompatActivity implements DialogCloseListener {
    RecyclerView tasksRecyclerView;
    ToDoAdapter taskAdapter;

    private FloatingActionButton fab;

    ArrayList<ToDoModel> taskList;


    private TextView Currentdate,CaptainName;

    //make node of task of current date;
    private DatabaseReference rootRef;
    private DatabaseReference tasksRef;
    private DatabaseReference todayRef;
    private FirebaseUser currentUser;
    private long totalTasks;
    private long dailyStreak;

    private long total_tasks;
    private long completed_tasks;

    private DialogInterface dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        createNotificationChannel();
        setAlarm();

//        all correct refrences
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));
        tasksRef = rootRef.child("tasks");


        //current date
        Currentdate = findViewById(R.id.curentdate);
        CaptainName = findViewById(R.id.captainName);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        Currentdate.setText(currentDate);
        DatabaseReference todayRef = tasksRef.child(currentDate);


        DatabaseReference userRef=rootRef;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String Uname = dataSnapshot.child("username").getValue(String.class);
                    String name="Hello "+Uname;
                    CaptainName.setText(name);
                    // Use the name value as needed
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


        //creating current day node inside all tasks
//    tasks
//        ->current date
//                ->totalTasks
//                -> daily streak   (agar yeh==total task h tok streak++ warna streak resets to 0)
//                ->task 1
//                    ->task id, status,name
//                ->task 2
//                ->task 3

        //method- 0

        todayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_tasks = 0;
                int completed_tasks = 0;

                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                    // Check if the current child node has child nodes with names "taskid", "name", and "status"
                    if (dataSnapshot2.hasChild("id")) {
                        total_tasks++;     //every task->counter increment
                        int status = dataSnapshot2.child("status").getValue(Integer.class);
                        if (status == 1) {
                            completed_tasks++; // increment completedTasks if the task is done (status=1)
                        }
                        taskAdapter.notifyDataSetChanged();
                    }
                }

                DatabaseReference dailyStreakRef = todayRef.child("dailyStreak");
                DatabaseReference totaltaskRef = todayRef.child("totalTasks");

                // Update the dailyStreak and totalTasks values in the database using setValue() method

                dailyStreakRef.setValue(completed_tasks);
                totaltaskRef.setValue(total_tasks);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read tasks.", databaseError.toException());
            }
        });



//        getSupportActionBar().hide();
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setHasFixedSize(false);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));

        taskList = new ArrayList<>();
        taskAdapter = new ToDoAdapter(todayRef, taskList,this);
        tasksRecyclerView.setAdapter(taskAdapter);



        fab = findViewById(R.id.fabteritoh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);

            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);


        todayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.hasChild("id")) {
                        ToDoModel task = dataSnapshot.getValue(ToDoModel.class);

                        taskList.add(task);
//                         Toast.makeText(Dashboard.this, "Task Added To Screen>>>", Toast.LENGTH_SHORT).show();
                    }
                }
                taskAdapter.notifyDataSetChanged();

                if (taskList.isEmpty()) {
                    Toast.makeText(Dashboard.this, "No tasks available, tasklist empty", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read tasks.", error.toException());
            }
        });





//        bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    break;

                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), Profile_user.class));
                    break;

            }
            return true;
        });

        Log.d(TAG, "Task List size: " + taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            Log.d(TAG, "Task " + i + ": " + taskList.get(i));
        }

        //recycler view scrolling problem
        tasksRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Check if the RecyclerView has enough space to display all items
                if (tasksRecyclerView.computeVerticalScrollRange() <= tasksRecyclerView.computeVerticalScrollExtent()) {
                    // The RecyclerView has enough space to display all items, so disable nested scrolling
                    tasksRecyclerView.setNestedScrollingEnabled(false);
                } else {
                    // The RecyclerView does not have enough space to display all items, so enable nested scrolling
                    tasksRecyclerView.setNestedScrollingEnabled(true);
                }

            }
        });
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {

    }

    private void createNotificationChannel() {
        System.out.println("step0");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Nandini";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel_id",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void setAlarm() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8); // 8am
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);

        if (Calendar.getInstance().after(cal)) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        System.out.println("step1");
        Intent intent = new Intent(Dashboard.this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        System.out.println("step2");

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        }
        System.out.println("step3");

        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();

    }


}
