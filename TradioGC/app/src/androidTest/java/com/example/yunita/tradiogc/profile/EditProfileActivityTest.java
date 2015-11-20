package com.example.yunita.tradiogc.profile;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yunita.tradiogc.login.LoginActivity;
import com.example.yunita.tradiogc.user.User;
import com.example.yunita.tradiogc.user.UserController;


public class EditProfileActivityTest extends ActivityInstrumentationTestCase2 {
    EditProfileActivity editProfileActivity;

    public EditProfileActivityTest() {
        super(com.example.yunita.tradiogc.profile.EditProfileActivity.class);
    }

    /**
     * Use Case 39
     * test for editing profile
     */
    public void testEditProfile() {
        // set a login user
        LoginActivity.USERLOGIN = new User();
        LoginActivity.USERLOGIN.setUsername("test");

        // start an EditProfileActivity
        editProfileActivity = (EditProfileActivity) getActivity();

        // check default info
        final EditText location_et = editProfileActivity.getLocation_et();
        final EditText email_et = editProfileActivity.getEmail_et();
        final EditText phone_et = editProfileActivity.getPhone_et();
        TextView username = editProfileActivity.getUsername();
        assertEquals(username.getText(), "test");
        assertEquals(location_et.getText().toString(), "");
        assertEquals(phone_et.getText().toString(), "");
        assertEquals(email_et.getText().toString(), "");

        // edit user info and save it
        editProfileActivity.runOnUiThread(new Runnable() {
            public void run() {
                location_et.setText("LocationTest");
                email_et.setText("EmailTest");
                phone_et.setText("phoneTest");
                Button save = editProfileActivity.getSave();
                save.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();

        // check the info after saving
        assertEquals(LoginActivity.USERLOGIN.getLocation(), "LOCATIONTEST");
        assertEquals(LoginActivity.USERLOGIN.getEmail(), "emailtest");
        assertEquals(LoginActivity.USERLOGIN.getPhone(), "phoneTest");

        // clear the data
        LoginActivity.USERLOGIN = new User();
        LoginActivity.USERLOGIN.setUsername("test");

        UserController userController = new UserController(editProfileActivity.getContext());
        Thread updateUserThread = userController.new UpdateUserThread(LoginActivity.USERLOGIN);
        updateUserThread.start();
    }


}