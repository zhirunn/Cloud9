package com.example.yunita.tradiogc.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yunita.tradiogc.MainActivity;
import com.example.yunita.tradiogc.R;
import com.example.yunita.tradiogc.SearchController;
import com.example.yunita.tradiogc.User;

public class LoginActivity extends Activity {

    public static boolean STATUS = false;
    public static User USERLOGIN = new User();

    private Context mContext = this;
    private LoginController loginController;
    private SearchController searchController;

    private LinearLayout login_view;
    private LinearLayout signup_view;
    private EditText username_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginController = new LoginController(mContext);
        searchController = new SearchController(mContext);

        login_view = (LinearLayout) findViewById(R.id.login_view);
        signup_view = (LinearLayout) findViewById(R.id.signUp_view);

        username_et = (EditText) findViewById(R.id.usernameEditText);

        if(STATUS){
            goToMain();
            finish();
        }
    }

    public void goToSignUp(View view) {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.pull_down);
        login_view.setVisibility(View.GONE);
        signup_view.setVisibility(View.VISIBLE);
        signup_view.startAnimation(anim);
    }

    public void goToLogin(View view) {
        signup_view.setVisibility(View.GONE);
        login_view.setVisibility(View.VISIBLE);
    }


    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    public void login(View view){

        final String username = username_et.getText().toString();

        // Execute the thread
        Thread thread = searchController.new GetUserLoginThread(username);
        thread.start();

        synchronized (thread) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (USERLOGIN == null) {
                Toast toast = Toast.makeText(mContext, "This username does not exist.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                goToMain();
            }
        }
    }

    public void signUp(View view) {
        String username = username_et.getText().toString();

        // Execute the thread
        Thread thread = loginController.new SignUpThread(username);
        thread.start();

        goToMain();
    }
}