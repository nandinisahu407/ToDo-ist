package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.DayViewDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Profile_user extends AppCompatActivity {
    private TextView monthYearTextView;
    private CalendarView calendarView;
    private Button analysebtn;

    LayoutInflater inflater;
    ViewGroup container;
    ArrayList<Calendar> calendarList;

    Integer streak;
    Integer longest_streak;
    Integer max_streak;

    private TextView  c_streak,l_streak;

    ArrayList<String> dates = new ArrayList<>();

    int total_days;


    private DatabaseReference rootRef;
    private DatabaseReference tasksRef;
    private DatabaseReference dateRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        c_streak = findViewById(R.id.current_streak);
        l_streak = findViewById(R.id.longest_streak);

        analysebtn=findViewById(R.id.analysebutton);
        analysebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile_user.this,Analyse_screen.class));

            }
        });

        Calendar calendar = Calendar.getInstance(); // Get the current date and time
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthYearString = monthYearFormat.format(calendar.getTime()); // Format the current date to month name and year

        monthYearTextView= findViewById(R.id.month_year_text_view);
        calendarView=findViewById(R.id.calendar_view);
        monthYearTextView.setText(monthYearString);

        calendar.add(Calendar.MONTH, 0); // Add one month to the current date

        calendarView.setDate(calendar.getTimeInMillis()); // Set the selected date in the CalendarView

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));
        tasksRef = rootRef.child("tasks");



        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                streak = 0;
                longest_streak = 0;
                max_streak=0;

                total_days = (int) snapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String selectedate = dataSnapshot.getKey();
                    System.out.println("\n\nDate in Analyse Screen: " + selectedate);
                    dates.add(selectedate);

                    int doneTasksCount = dataSnapshot.child("dailyStreak").getValue(Integer.class);
                    int totalTasksCount = dataSnapshot.child("totalTasks").getValue(Integer.class);
                    int notDoneTasksCount = totalTasksCount - doneTasksCount;
                    System.out.println("\n\ndone tasks-> " + doneTasksCount + ", not done tasks-> " + notDoneTasksCount);

                    if(doneTasksCount==totalTasksCount && totalTasksCount!=0){
                        streak+=1;
                    }
                    else{

                        if(longest_streak<=streak){

                            longest_streak=streak;
                        }
                        streak=0;
                    }

                    System.out.println("\n\ncurrent streak-> " + streak + ", longest streak-> " + longest_streak);

                }
                String s1="Current Streak: "+streak;
                String s2="Longest Streak: "+longest_streak;
                System.out.println("\n\n\noutside method current streak-> " + streak + ", longest streak-> " + longest_streak);


                c_streak.setText(s1);
                c_streak.setTextColor(Color.rgb(237, 102, 102));
                l_streak.setText(s2);
                l_streak.setTextColor(Color.rgb(177, 252, 3));




            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);


//        Drawable customDrawable = getResources().getDrawable(R.drawable.calendar_badge3);
//
//        CustomCalendarView calendarView = findViewById(R.id.calendar_view);
//
//        // Create a list of dates that you want to change the drawable of
//        List<Calendar> dates = new ArrayList<>();
//        Calendar cal1 = Calendar.getInstance();
//        cal1.set(2023, Calendar.APRIL, 1); // set the date to change drawable
//        dates.add(cal1);
//        Calendar cal2 = Calendar.getInstance();
//        cal2.set(2023, Calendar.APRIL, 4); // set the date to change drawable
//        dates.add(cal2);
//        Calendar cal3 = Calendar.getInstance();
//        cal3.set(2023, Calendar.APRIL, 12); // set the date to change drawable
//        dates.add(cal3);
//
//        // Change the drawable of the cells corresponding to the specified dates
//        for (Calendar date : dates) {
//            long timeInMillis = date.getTimeInMillis();
//            calendarView.setDateCellDrawable(R.drawable.calendar_badge3, timeInMillis);
//        }
//
//        // Refresh the calendar view to apply the changes
//        calendarView.invalidate();



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(selectedDate.getTime());
                System.out.println("Date in profile");
                System.out.println(formattedDate);

                dateRef = tasksRef.child(formattedDate);
                dateRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean verify = false;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.hasChild("task")) {
                                verify = true;    //task h

                            }
                        }

                        if (verify == true) {
                            Intent intent = new Intent(Profile_user.this, TaskListActivity.class);
                            intent.putExtra("selectedDate", formattedDate);
                            startActivity(intent);

                        } else if (!snapshot.exists() || verify == false) {
                            // animation -> date ki node hi nahi ya abhi tak tasks add nhi hui h(eg current date)

                            Intent intent = new Intent(Profile_user.this, NotaskActivity.class);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


        });

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

