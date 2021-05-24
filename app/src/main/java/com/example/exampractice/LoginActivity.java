package com.example.exampractice;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exampractice.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private Button loginB;
    private TextView forgotPassB, signupB;
    private FirebaseAuth mAuth;
    private Dialog progressDialog;
    private TextView dialogText;
    private RelativeLayout gSignB;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        loginB = findViewById(R.id.loginB);
        forgotPassB = findViewById(R.id.signupB);
        signupB = findViewById(R.id.signupB);
        gSignB = findViewById(R.id.g_signB);


        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("686221012979-6m8qt4losrv882bkh094k1lu9c0gsru5.apps.googleusercontent.com")
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateData())
                {
                    login();
                }

            }
        });

        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        gSignB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

    }

    private boolean validateData()
    {


        if(email.getText().toString().isEmpty())
        {
            email.setError("Enter a valid Email");
            return  false;
        }

        if(pass.getText().toString().isEmpty())
        {
            pass.setError("Enter Password");
            return false;
        }
        return true;
    }

    private void login()
    {
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startLoadingDialog();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            DbQuery.loadData(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {

                                    loadingDialog.dismissDialog();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void onFailure() {

                                    Toast.makeText(LoginActivity.this, "Something went wrong please try again later",
                                            Toast.LENGTH_SHORT).show();



                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();

                        }

                        // ...
                    }
                });

    }


    private void googleSignIn()

    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startLoadingDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Google Sign In Success",Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                DbQuery.createUserData(user.getEmail(), user.getDisplayName(), new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {

                                        DbQuery.loadData(new MyCompleteListener() {
                                            @Override
                                            public void onSuccess() {

                                                loadingDialog.dismissDialog();

                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onFailure() {
                                                Toast.makeText(LoginActivity.this, "Something went wrong please try again later",
                                                        Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismissDialog();


                                            }
                                        });



                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(LoginActivity.this, "Something went wrong please try again later",
                                                Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismissDialog();

                                    }
                                });
                            }
                            else
                            {
                                DbQuery.loadData(new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        loadingDialog.dismissDialog();

                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onFailure() {

                                        Toast.makeText(LoginActivity.this, "Something went wrong please try again later",
                                                Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismissDialog();


                                    }
                                });
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}