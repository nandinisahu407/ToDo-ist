package com.example.todo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.todo.Model.ToDoModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG="ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private Context context;
    private DatabaseReference databaseRef;
    private DatabaseReference rootRef;

    private FirebaseUser currentUser;


    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,R.style.DialogStyle);

        // Get reference to Firebase Realtime Database

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        databaseRef = rootRef.child("tasks").child(currentDate);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.new_task,container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newTaskText=getView().findViewById(R.id.newTaskText);
        newTaskSaveButton=getView().findViewById(R.id.newTaskButton);

        boolean isUpdate=false;

        final Bundle bundle=getArguments();
        if (bundle!=null){
            isUpdate=true;
            String task=bundle.getString("task");
            newTaskText.setText(task);

            if(task.length()>0){
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));

            }
        }
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                    Toast.makeText(getContext(), "Task needed !!", Toast.LENGTH_SHORT).show();
                }

                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=newTaskText.getText().toString();

                if(finalIsUpdate){
                    //update the task

                      String  id = bundle.getString("id");
                      databaseRef.child(String.valueOf(id)).child("task").setValue(text);

                }

                else if(text.isEmpty()){
                    Toast.makeText(getContext(), "Task needed !!", Toast.LENGTH_SHORT).show();

                }

                else{
                    //adding new task
                    ToDoModel task=new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    String id=databaseRef.push().getKey();
                    task.setId(id);

                        // Push the new task to Firebase Realtime Database
                    databaseRef.child(id).setValue(task);
                    Toast.makeText(getContext(), "Task Added Successfully!!✅", Toast.LENGTH_SHORT).show();

                }
                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity=getActivity();
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

}
