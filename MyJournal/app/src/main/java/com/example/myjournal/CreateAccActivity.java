package com.example.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class CreateAccActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText,ConfirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
TextView loginBtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        ConfirmPasswordEditText = findViewById(R.id.Confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progressbar);
        loginBtnTextView = findViewById(R.id.login_textview_btn);


        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> finish());
    }

    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = ConfirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if(! isValidated){
            return;
        }
        createAccountInFirebase(email,password);

    }
    void createAccountInFirebase(String email, String password){
            changeInProgress(true);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccActivity.this,

                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Utility.showToast(CreateAccActivity.this, "Successful");
                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                firebaseAuth.signOut();
                                finish();

                            } else {
                                Utility.showToast(CreateAccActivity.this, task.getException().getLocalizedMessage());


                            }
                        }
                     }
            );
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);

        }else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword){

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email doesn't match");
            return false;
        }
        if(password.length()<4){
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            ConfirmPasswordEditText.setError("Password does not match");
            return false;
        }
        return true;
    }
}