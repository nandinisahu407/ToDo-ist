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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.AddNewTask;
import com.example.todo.Dashboard;
import com.example.todo.MainActivity;
import com.example.todo.Model.ToDoModel;
import com.example.todo.R;
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

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private ArrayList<ToDoModel> todoList;
    private Dashboard activity;
    private DatabaseReference databaseReference;


    public ToDoAdapter(DatabaseReference databaseReference, ArrayList<ToDoModel> todoList,Dashboard activity){
        this.databaseReference = databaseReference;
        this.activity=activity;
        this.todoList=todoList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout,parent,false);

        return  new ViewHolder(itemView);

    }

    public void onBindViewHolder(ViewHolder holder,int position) {
        ToDoModel item = todoList.get(position);
        holder.taskTextscreen.setText(item.getTask());
        holder.taskstatus.setOnCheckedChangeListener(null);     // reset the listener to null
        holder.taskstatus.setChecked(toBoolean(item.getStatus()));
        holder.taskstatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override 
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                databaseReference.child(item.getId()).child("status").setValue(isChecked ? 1 : 0);
                item.setStatus(isChecked?1:0);

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

    public Context getContext(){
        return activity;
    }


    public void deleteItem(int position){
        ToDoModel item=todoList.get(position);
        databaseReference.child(item.getId()).removeValue();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        ToDoModel item=todoList.get(position);
        Bundle bundle=new Bundle();
        bundle.putString("id",item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment=new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(),AddNewTask.TAG);

    }
}
