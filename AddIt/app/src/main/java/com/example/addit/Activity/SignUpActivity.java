package com.example.addit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.addit.Activity.DashboardActivity;
import com.example.addit.Model.User;
import com.example.addit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.addit.Validation.isMatchPasswords;
import static com.example.addit.Validation.isNotEmpty;
import static com.example.addit.Validation.isValidEmail;
import static com.example.addit.Validation.isValidPassword;

public class SignUpActivity extends AppCompatActivity {
    private EditText user_name;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private Button sign_up_btn;
    private FirebaseAuth _Auth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        _Auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);

        user_name=findViewById(R.id.username_txt);
        email=findViewById(R.id.email_txt);
        password=findViewById(R.id.password_txt);
        confirm_password=findViewById(R.id.pass_confirm_txt);
        sign_up_btn=findViewById(R.id.sign_up_btn);


                sign_up_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String user_name_txt=user_name.getText().toString().trim();
                        String email_txt=email.getText().toString().trim();
                        String password_txt=password.getText().toString().trim();
                        String confirm_password_txt=confirm_password.getText().toString().trim();


                        if(!isNotEmpty(user_name_txt,user_name)||!isValidEmail(email_txt,email)||!isValidPassword(password_txt,password)||!isValidPassword(confirm_password_txt,confirm_password)
                            ||!isMatchPasswords(password_txt, confirm_password_txt, confirm_password)) return;

                        dialog.setMessage("Processing...");
                        dialog.show();

                        _Auth.createUserWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d("Successful", "onComplete: sucsess");
                                    FirebaseUser firebaseUser=_Auth.getCurrentUser();
                                    String uid=firebaseUser.getUid();
                                    Log.d("UID", "onComplete: user id="+uid);
                                    User user=new User(uid,firebaseUser.getEmail(),user_name_txt);
                                    Log.d("UID", "onComplete: user id="+user.toString());

                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("Users");
                                    db.child(uid).setValue(user);
                                    Toast.makeText(getApplicationContext(),"Successful sign up",Toast.LENGTH_LONG);
                                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Failed sign up...",Toast.LENGTH_LONG);
                                    Log.d("fail", "onComplete: fail");
                                    dialog.dismiss();


                                }
                            }
                        });
                    }
                });
   }
}
