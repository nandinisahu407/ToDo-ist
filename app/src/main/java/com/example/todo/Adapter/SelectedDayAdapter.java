package com.example.todo.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.AddNewTask;
import com.example.todo.Dashboard;
import com.example.todo.MainActivity;
import com.example.todo.Model.ToDoModel;
import com.example.todo.R;
import com.example.todo.TaskListActivity;
import com.example.todo.Utils.DatabaseHandler;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectedDayAdapter extends RecyclerView.Adapter<SelectedDayAdapter.ViewHolder> {

    ArrayList<ToDoModel> todoList;
    Context context;

    public SelectedDayAdapter(ArrayList<ToDoModel> todoList,Context context){
        this.context=context;
        this.todoList=todoList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView= LayoutInflater.from(context).inflate(R.layout.task_layout,parent,false);

        return  new ViewHolder(itemView);

    }

    public void onBindViewHolder(ViewHolder holder,int position) {
        ToDoModel item = todoList.get(position);
        holder.taskTextscreen.setText(item.getTask());
        holder.taskstatus.setChecked(toBoolean(item.getStatus()));

        holder.taskstatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, "Sorry cannot change the status !!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public ToDoModel getItem(int position) {
        return todoList.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox taskstatus;
        TextView taskTextscreen;

        public ViewHolder(@NonNull View view) {
            super(view);
            taskstatus=view.findViewById(R.id.todocheckBox);
            taskTextscreen=view.findViewById(R.id.recyclerTaskText);
            taskstatus.setClickable(false);
            taskstatus.setFocusable(false);
        }
    }

    @Override
    public int getItemCount() {
        if (todoList == null) {
            return 0;
        } else {
            return todoList.size();
        }
    }
    private boolean toBoolean(int n){
        return n!=0;
    }

    public void setTask(ArrayList<ToDoModel> todoList){
        this.todoList=todoList;
        notifyDataSetChanged();

    }


}
