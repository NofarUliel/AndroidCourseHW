package com.example.addit;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

public class Validation {

    public static boolean isValidEmail(String email_txt, EditText email){

        if (TextUtils.isEmpty(email_txt)) {
            email.setError("Required field..");
            email.setFocusable(true);
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email_txt).matches()){
            email.setError("Invalid email");
            email.setFocusable(true);
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(String password_txt, EditText password){

        if (TextUtils.isEmpty(password_txt)) {
            password.setError("Required field..");
            password.setFocusable(true);
            return false;
        }
        if(password.length()<6){
            password.setError("Password length at least 6 characters");
            password.setFocusable(true);
            return false;
        }
        return true;
    }

    public static boolean isMatchPasswords(String password_txt,String confirm_password_txt,EditText confirm_password){
        if(!password_txt.equals(confirm_password_txt)){
            confirm_password.setError("Passwords not match");
            return false;
        }
        return true;
    }
    public static boolean isNotEmpty(String field_txt, EditText field) {

        if (TextUtils.isEmpty(field_txt)) {
            field.setError("Required field..");
            field.setFocusable(true);
            return false;
        }
        return true;
    }

}
