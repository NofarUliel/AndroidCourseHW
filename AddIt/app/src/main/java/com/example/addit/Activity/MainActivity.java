package com.example.addit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.addit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import static com.example.addit.Validation.isValidEmail;
import static com.example.addit.Validation.isValidPassword;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText email, password;
    private FirebaseAuth _Auth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email = findViewById(R.id.email_txt);
        password = findViewById(R.id.password_txt);

        _Auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);

    }


@Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.log_in_btn:
                String email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();


                if(!isValidEmail(email_txt,email)||!isValidPassword(password_txt, password)) return ;
                dialog.setMessage("Processing...");
                dialog.show();

                _Auth.signInWithEmailAndPassword(email_txt, password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Successfully log in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed log in :(\nYour email or password is wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();


                        }
                    }
                });
                break;

            case R.id.sign_up_btn:
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;
            default:
                    break;

        }
    }
}