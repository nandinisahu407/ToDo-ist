package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//have firebase auth+realtime database
public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupName,signupEmail,signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth=FirebaseAuth.getInstance();
        signupName=findViewById(R.id.signup_name);
        signupEmail=findViewById(R.id.signup_email);
        signupPassword=findViewById(R.id.signup_password);

        signupButton=findViewById(R.id.signup_button);
        loginRedirectText=findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database=FirebaseDatabase.getInstance();
                reference=database.getReference("users");

                String username=signupName.getText().toString();
                String email=signupEmail.getText().toString().trim();
                String pass=signupPassword.getText().toString().trim();

                if(username.isEmpty()){
                    signupName.setError("Username Required");
                }
                if(email.isEmpty()){
                    signupEmail.setError("Email Required");
                }
                if(pass.isEmpty()){
                    signupPassword.setError("Password Required");
                }
                else{
                    //bring all user
                    //check for user id and email doesn't required

                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                HelperClass helperClass=new HelperClass(username,email,pass);
//                                reference.child(username).setValue(helperClass);
                                  reference.child(email.replace(".",",")).setValue(helperClass);

                                Toast.makeText(SignUpActivity.this, "SignUp Successfull 😍", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "SignUp Failed 😟"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(SignUpActivity.this , LoginActivity.class));

            }
        });

    }
}

