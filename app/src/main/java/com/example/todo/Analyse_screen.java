package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Analyse_screen extends AppCompatActivity {

    PieChart pcha;
    int[] colorClassArray=new int[]{Color.LTGRAY,Color.BLUE,Color.GREEN,Color.YELLOW,Color.RED,Color.CYAN};


    BarChart bar;
    BarData data;
    BarDataSet barDataSet1, barDataSet2;

    Integer final_totaltasks;
    Integer final_completedtask;
    Integer final_notdonetasks;

    Float percent_complete;

    ArrayList<PieEntry> PieArraylist;

    ArrayList<String> dates = new ArrayList<>();
    ArrayList<BarEntry> doneEntries = new ArrayList<>();
    ArrayList<BarEntry> not_doneEntries = new ArrayList<>();
    ArrayList<Integer> doneTasks = new ArrayList<>();
    ArrayList<Integer> notDoneTasks = new ArrayList<>();


    ArrayList<BarEntry> information;
    private TextView  usermsg,completelabel,totallabel,notdonelabel;
    private DatabaseReference rootRef;
    private DatabaseReference tasksRef;
    private FirebaseUser currentUser;

    int total_days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_screen);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".", ","));
        tasksRef = rootRef.child("tasks");

        ArrayList<String> dates = new ArrayList<String>();
        bar = findViewById(R.id.barchart);
        bar.setBackgroundColor(Color.WHITE);

        pcha=findViewById(R.id.piechart);

        usermsg = findViewById(R.id.userreportmsg);
        totallabel = findViewById(R.id.totaltasklabel);
        completelabel = findViewById(R.id.completetasklabel);
        notdonelabel = findViewById(R.id.pendingtasklabel);



        PieArraylist = new ArrayList<>();

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates.clear();
                doneTasks.clear();
                notDoneTasks.clear();

                final_totaltasks=0;
                final_completedtask=0;
                final_notdonetasks=0;

                doneEntries.clear();
                not_doneEntries.clear();
                total_days = (int) snapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String selectedate = dataSnapshot.getKey();
                    System.out.println("\n\nDate in Analyse Screen: " + selectedate);
                    dates.add(selectedate);

                    int doneTasksCount = dataSnapshot.child("dailyStreak").getValue(Integer.class);
                    int totalTasksCount = dataSnapshot.child("totalTasks").getValue(Integer.class);
                    int notDoneTasksCount = totalTasksCount - doneTasksCount;
                    System.out.println("\n\ndone tasks-> " + doneTasksCount + ", not done tasks-> " + notDoneTasksCount);

                    final_totaltasks+=totalTasksCount;
                    final_completedtask+=doneTasksCount;
                    final_notdonetasks+=notDoneTasksCount;

                    doneTasks.add(doneTasksCount);
                    notDoneTasks.add(notDoneTasksCount);
                }

                for (int i = 0; i < doneTasks.size(); i++) {
                    doneEntries.add(new BarEntry(i, doneTasks.get(i)));
//                    not_doneEntries.add(new BarEntry(i, notDoneTasks.get(i)));
                }
                for (int i = 0; i < doneTasks.size(); i++) {
                    not_doneEntries.add(new BarEntry(i, notDoneTasks.get(i)));
                }

                percent_complete=(float)final_completedtask/final_totaltasks;
                percent_complete*=100;



                System.out.println("\n\nTotal task in piechart: " + final_totaltasks);
                System.out.println("\n\ncompleted task in piechart: " + final_completedtask);
                System.out.println("\n\nNot done task in piechart: " + final_notdonetasks);
                System.out.println("\n\nCompletion Percentage: " + percent_complete);

                String s1="Total Tasks Added: "+final_totaltasks;
                String s2="Completed Tasks: "+final_completedtask;
                String s3="Pending Tasks: "+final_notdonetasks;

                totallabel.setText(s1);
                completelabel.setText(s2);
                notdonelabel.setText(s3);

                String u1="You Are Too Lazy!!😴,\nYou Can Do Better!!👍🏻" ;  //msg for below 30%
                String u2="You Are Good!!\nKeep Going 🚀" ;  //msg for 50%
                String u3="You Are Awesome !!\n Be Unstoppable 🚀" ;     //msg for above 75%
                
                if(percent_complete<=30.0){
                    usermsg.setText(u1);
                    usermsg.setTextColor(Color.RED);
                }
                else if(percent_complete>30.0 && percent_complete<75.0){
                    usermsg.setText(u2);
                    usermsg.setTextColor(Color.rgb(177, 252, 3));
                }
                else{
                    usermsg.setText(u3);
                    usermsg.setTextColor(Color.rgb(195, 162, 224));
                }




                //piechart
                PieArraylist.add(new PieEntry(final_completedtask,"Completed"));
                PieArraylist.add(new PieEntry(final_notdonetasks,"Not Done"));

                PieDataSet dataSet = new PieDataSet(PieArraylist, "");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData p = new PieData(dataSet);
                p.setValueTextColor(Color.BLACK);
//                p.setValueTextSize(20f);

                pcha.setData(p);
                pcha.getDescription().setEnabled(false);
//                pcha.setDrawEntryLabels(true);
////                pcha.setDrawHoleEnabled(true);
                pcha.setUsePercentValues(true);
                pcha.setCenterText("Total Analysis");
                pcha.setCenterTextSize(15);
                pcha.invalidate();






                barDataSet1 = new BarDataSet(doneEntries, "Done Tasks");
                barDataSet1.setColors(Color.YELLOW);

                barDataSet2 = new BarDataSet(not_doneEntries, "Not Done");
                barDataSet2.setColors(Color.BLACK);

                data = new BarData(barDataSet1, barDataSet2);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(12f);

                bar.setData(data);
                XAxis xaxis = bar.getXAxis();

                bar.setAutoScaleMinMaxEnabled(true);
                bar.getDescription().setEnabled(false);         // hide the description label
                bar.setDrawGridBackground(false);       // hide the background grid lines
                bar.setTouchEnabled(true);   // enable touch gestures (e.g. scrolling and zooming)
                bar.setDragEnabled(true);   // enable dragging the chart
                bar.setScaleEnabled(true);  // enable scaling the chart
                bar.setPinchZoom(false);    // enable zooming via pinch gestures
                bar.getAxisRight().setEnabled(false);   //pehle false th
                bar.getAxisLeft().setDrawGridLines(false);
                bar.getXAxis().setDrawGridLines(false);

                xaxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                xaxis.setCenterAxisLabels(true);
                xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xaxis.setGranularity(1);
                xaxis.setGranularityEnabled(true);

                bar.setDragEnabled(true);
                bar.setVisibleXRangeMaximum(3);

                float barspace = 0.1f;
                float groupspace = 0.5f;

                data.setBarWidth(0.15f);
                bar.getXAxis().setAxisMinimum(0);
//                bar.getXAxis().setAxisMaximum(0 + bar.getBarData().getGroupWidth(groupspace, barspace) * total_days);  //multiply by 5 kyuki 5 group h na
                bar.getAxisLeft().setAxisMinimum(0);

                bar.groupBars(0, groupspace, barspace);
                bar.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
