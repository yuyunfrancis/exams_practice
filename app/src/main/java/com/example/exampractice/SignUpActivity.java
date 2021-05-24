package com.example.exampractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.exampractice.R.id.progressBar;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, email, phne_num, pass, confirmPass;
    private TextView loginB;
    private Button sigUpB;
    private ImageView backB;
    private FirebaseAuth mAuth;
    private String emailStr, passStr, confirmPassStr, nameStr;
    private Dialog progressDialog;
    private TextView dialogText;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.username);
        email = findViewById(R.id.emailID);
        pass = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirm_pass);
        sigUpB = findViewById(R.id.signup_B);
        backB = findViewById(R.id.backB);
        loginB = findViewById(R.id.loginB);


        mAuth = FirebaseAuth.getInstance();


        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        sigUpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    signUpNewUser();
                }


            }
        });

    }

    private boolean validate()
    {
        nameStr = name.getText().toString().trim();
        passStr = pass.getText().toString().trim();
        emailStr = email.getText().toString().trim();
        confirmPassStr = confirmPass.getText().toString().trim();

        if(nameStr.isEmpty()){
            name.setError("Enter Your Name");
            return false;
        }
        if(emailStr.isEmpty())
        {
            email.setError("Enter a Valid Email");
            return false;
        }

        if(passStr.isEmpty()){
            pass.setError("Enter Password");
            return false;
        }

        if(confirmPassStr.isEmpty())
        {
            confirmPass.setError("Enter Password");
            return false;
        }

        if(passStr.compareTo(confirmPassStr) != 0)
        {
            Toast.makeText(SignUpActivity.this,"Make sure passwords Match", Toast.LENGTH_SHORT);
            return false;
        }

        return true;

    }


    private void signUpNewUser()
    {
        final LoadingDialog loadingDialog = new LoadingDialog(SignUpActivity.this);
        loadingDialog.startLoadingDialog();
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT);


                            DbQuery.createUserData(emailStr, nameStr, new MyCompleteListener() {
                                @Override
                                public void onSuccess() {

                                    DbQuery.loadData(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            loadingDialog.dismissDialog();

                                            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            SignUpActivity.this.finish();

                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(SignUpActivity.this, "Something went wrong please try again later ",
                                                    Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();

                                        }
                                    });


                                }

                                @Override
                                public void onFailure() {

                                    Toast.makeText(SignUpActivity.this, "Something went wrong please try again later ", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissDialog();
                                }
                            });





                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();

                        }

                        // ...
                    }
                });
    }


}
