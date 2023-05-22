package com.example.todo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todo.Model.ToDoModel;
import com.example.todo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//public class DatabaseHandler extends SQLiteOpenHelper {
//
//    private static final int VERSION=1;
//    private static final String NAME="toDoListDatabase";
//    private static final String TODO_TABLE="todo";
//    private static final String ID="id";
//    private static final String TASK="task";
//    private static final String STATUS="status";
//    private static final String CREATE_TODO_TABLE="CREATE TABLE"+TODO_TABLE+"("+ID+"INTEGER PRIMARY KEY AUTOINCREMENT,"
//            +TASK+"TEXT,"+STATUS+"INTEGER)";
//
//    private SQLiteDatabase db;
//    public DatabaseHandler(Context context){
//        super(context,NAME,null,VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db){
//        db.execSQL(CREATE_TODO_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
//        //drop the older table
//        db.execSQL("DROP TABLE IF EXISTS"+ TODO_TABLE);
//
//        //create table again
//        onCreate(db);
//
//    }
//
//    public void openDatabase(){
//        db=this.getWritableDatabase();
//
//    }
//
//    public void insertTask(ToDoModel task){
//        ContentValues cv=new ContentValues();
//        cv.put(TASK,task.getTask());
//        cv.put(STATUS,0);
//        db.insert(TODO_TABLE,null,cv);
//    }
//
//    public List<ToDoModel> getAllTasks(){
//        List<ToDoModel> taskList=new ArrayList<>();
//        Cursor cur=null;
//        db.beginTransaction();
//        try{
//            cur=db.query(TODO_TABLE,null,null,null,null,null,null,null);
//                if(cur!=null){
//                    if(cur.moveToFirst()){
//                        do{
//                            ToDoModel task=new ToDoModel();
//                            task.setId(cur.getInt(cur.getColumnIndex(ID)));
//                            task.setTask(cur.getString(cur.getColumnIndex(TASK)));
//                            task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
//                            taskList.add(task);
//
//                        }while(cur.moveToNext());
//                    }
//                }
//        }
//        finally{
//            db.endTransaction();
//            cur.close();
//
//        }
//        return taskList;
//
//    }
//
//    public void updateStatus(int id,int status){
//        ContentValues cv=new ContentValues();
//        cv.put(STATUS,status);
//        db.update(TODO_TABLE,cv,ID+"=?",new String[]{String.valueOf(id)});
//    }
//
//    public void updateTask(int id,String task){
//        ContentValues cv=new ContentValues();
//        cv.put(TASK,task);
//        db.update(TODO_TABLE,cv,ID+"=?",new String[]{String.valueOf(id)});
//    }
//
//    public void deleteTask(int id){
//        db.delete(TODO_TABLE,ID+"=?",new String[]{String.valueOf(id)});
//    }
//
//}

public class DatabaseHandler {
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private DatabaseReference tasksRef;

    private DatabaseReference mDatabase;


    public DatabaseHandler(Context context) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));
        tasksRef = rootRef.child("tasks");


        //current date

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        mDatabase = tasksRef.child(currentDate);

    }

    public void insertTask(ToDoModel task) {
        String taskId = mDatabase.push().getKey();
        task.setId(taskId);
        mDatabase.child(taskId).setValue(task);
    }

    public ArrayList<ToDoModel> getAllTasks() {
        final ArrayList<ToDoModel> taskList = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ToDoModel task = postSnapshot.getValue(ToDoModel.class);
                    taskList.add(task);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return taskList;
    }

    public void updateStatus(String id, int status) {
        mDatabase.child(id).child("status").setValue(status);
    }

    public void updateTask(String id, String task) {
        mDatabase.child(id).child("task").setValue(task);
    }

    public void deleteTask(String id) {
        mDatabase.child(id).removeValue();
    }

}

