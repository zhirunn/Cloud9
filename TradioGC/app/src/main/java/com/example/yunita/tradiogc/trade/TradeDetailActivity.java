package com.example.yunita.tradiogc.trade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yunita.tradiogc.R;
import com.example.yunita.tradiogc.inventory.Inventory;
import com.example.yunita.tradiogc.inventory.Item;
import com.example.yunita.tradiogc.login.LoginActivity;
import com.example.yunita.tradiogc.record.RecordActivity;
import com.example.yunita.tradiogc.user.User;
import com.example.yunita.tradiogc.user.UserController;
import com.example.yunita.tradiogc.email.GMailSender;

/**
 * This activity handles viewing trade details, and accepting/declining trades.
 */
public class TradeDetailActivity extends AppCompatActivity {
    private TextView tradeWith;
    private TextView ownerItemName;
    private TextView ownerItemPrice;
    private TextView ownerItemDescription;
    private ImageView ownerItemPhoto;
    private ListView itemsOfferedList;
    private LinearLayout offeredTradePanel;
    private LinearLayout ownerPanel;
    private TextView status;

    private ArrayAdapter<Item> itemsOfferedArrayAdapter;
    private Inventory itemsOffered = new Inventory();
    private UserController userController;
    private Context context = this;
    private Trade trade = new Trade();
    private String anotherUsername="";
    private User anotherUser = new User();
    private boolean counterTrade = false;
    private String tab_title;

    private EditText comments_et;
    private AlertDialog acceptBuilder;
    private AlertDialog counterBuilder;

    /**
     * Gets the "Accept Trade" button.
     *
     * @return Button "Accept Trade" button
     */
    public Button getAcceptTradeButton(){
        return (Button) findViewById(R.id.accept_trade_button);
    }

    /**
     * Gets the "Decline Trade" button.
     *
     * @return Button "Decline Trade" button
     */
    public Button getDeclineTradeButton(){
        return (Button) findViewById(R.id.decline_trade_button);
    }

    /**
     * Gets the owner's comments after accepting the trade.
     *
     * @return comments_et owner's comments after accepting the trade
     */
    public EditText getComments_et() {
        return comments_et;
    }

    /**
     * Gets the alert dialog to enter a comment when a trade is accepted.
     *
     * @return acceptBuilder alert dialog to enter comments
     */
    public AlertDialog getAcceptBuilder() {
        return acceptBuilder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        itemsOfferedList = (ListView) findViewById(R.id.item_offered_list_view);
        tradeWith = (TextView) findViewById(R.id.trade_with);

        ownerItemName = (TextView) findViewById(R.id.ownerItemName);
        ownerItemPrice = (TextView) findViewById(R.id.ownerItemPrice);
        ownerItemDescription = (TextView) findViewById(R.id.ownerItemDescription);
        ownerItemPhoto = (ImageView) findViewById(R.id.ownerItemPhoto);
        offeredTradePanel = (LinearLayout) findViewById(R.id.offered_trade_panel);
        ownerPanel = (LinearLayout) findViewById(R.id.owner_panel);
        status = (TextView) findViewById(R.id.status);

        userController = new UserController(context);
    }

    @Override
    protected void onStart() {
        super.onStart();

        itemsOfferedArrayAdapter = new ArrayAdapter<Item>(this, R.layout.inventory_list_item, itemsOffered);
        itemsOfferedList.setAdapter(itemsOfferedArrayAdapter);

        // Gets trade by index
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null) {
                int tradeId = intent.getExtras().getInt("trade_id");
                trade = LoginActivity.USERLOGIN.getTrades().findTradeById(tradeId);
                tab_title = intent.getExtras().getString("tab_title");
            }
        }

        if (LoginActivity.USERLOGIN.getUsername().equals(trade.getBorrower())){
            anotherUsername = trade.getOwner();
            counterTrade = true;
        } else {
            anotherUsername = trade.getBorrower();
        }

        // Sets trade from
        tradeWith.setText("Trade with " + anotherUsername);

        tradeWith.setTypeface(null, Typeface.BOLD);


        // set item photo and information
        //LOAD PHOTO
        //Bitmap itemPhoto = decodeImage(trade.getOwnerItem().getPhotos());
        //LOAD PHOTO
        //ownerItemPhoto.setImageBitmap(itemPhoto);
        ownerItemName.setText(trade.getOwnerItem().getName() + "\n" +
                "Owner: " + trade.getOwner());
        ownerItemPrice.setText("$"+Double.toString(trade.getOwnerItem().getPrice()) + " x " + trade.getOwnerItem().getQuantity());
        ownerItemDescription.setText(trade.getOwnerItem().getDesc());

        // set trade status
        status.setText(String.valueOf(trade.getStatus().charAt(0)).toUpperCase() + trade.getStatus().substring(1));

        // set items offered
        itemsOffered.addAll(trade.getBorrowerItems());

        // set panel showed
        if (trade.getStatus().equals("offered")) {
            offeredTradePanel.setVisibility(View.VISIBLE);
        } else if (trade.getStatus().equals("accepted") && trade.getOwner().equals(LoginActivity.USERLOGIN.getUsername())) {
            ownerPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Activity finishes automatically if the user offers a counter trade.
     *
     * @param requestCode   request code for the sender that will be associated
     *                      with the result data when it is returned
     * @param resultCode    the integer result code returned by the child activity
     * @param data          an intent that can return result data to the caller
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (tab_title != null) {
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("tab_title", tab_title);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    finish();
    }

    /**
     * Called when the user presses the "Accept Trade" button.
     * <p>This method is used to set the trade as "accepted" and call a dialog to enter a comment.
     *
     * @param view "Accept Trade" button
     */
    public void accept(View view){
        // Creates a dialog asking the user to enter a comment
        acceptBuilder = new AlertDialog.Builder(this).create();
        comments_et = new EditText(this);
        comments_et.setHint("say something here...");

        acceptBuilder.setTitle("Comments:");
        acceptBuilder.setView(comments_et);
        acceptBuilder.setButton(AlertDialog.BUTTON_POSITIVE, "SEND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                trade.setStatus("accepted");
                LoginActivity.USERLOGIN.getNotifications().remove(LoginActivity.USERLOGIN.getNotifications().findNotificationById(trade.getId()));
                Thread updateUserThread = userController.new UpdateUserThread(LoginActivity.USERLOGIN);
                updateUserThread.start();

                synchronized (updateUserThread) {
                    try {
                        updateUserThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                Thread replyThread = new ReplyThread("accepted");
                replyThread.start();
                synchronized (replyThread) {
                    try {
                        replyThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String comments = formatEmail(comments_et.getText().toString());

                dialog.dismiss();

                Thread emailThread = new EmailThread(comments, anotherUser.getEmail(), LoginActivity.USERLOGIN.getEmail());
                emailThread.start();
                onBackPressed();
            }
        });
        acceptBuilder.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        acceptBuilder.show();
    }

    /**
     * Formats the email message.
     *
     * @param tradeComment owner's comment
     * @return String
     */
    public String formatEmail(String tradeComment){
        String comments = "Comment from " + LoginActivity.USERLOGIN.getUsername() + ": " + tradeComment + "\n";
        comments += "======================================\n ";
        comments += "Trade Detail:\n ";
        comments += "======================================\n ";
        comments += trade.getOwner() + "'s item: " + trade.getOwnerItem().getName()+"\n";
        comments += trade.getBorrower() + "'s offer(s): " + trade.getBorrowerItems();
        return comments;
    }

    /**
     * Called when the user presses the "Decline Trade" button.
     * <p>This method is used to set the trade as "declined" and call a prompt dialog
     * asking if the owner wants to make a counter trade.
     *
     * @param view "Decline Trade" button
     */
    public void decline(View view){
        LoginActivity.USERLOGIN.getNotifications().remove(LoginActivity.USERLOGIN.getNotifications().findNotificationById(trade.getId()));
        LoginActivity.USERLOGIN.getTrades().remove(trade);
        Thread updateUserThread = userController.new UpdateUserThread(LoginActivity.USERLOGIN);
        updateUserThread.start();

        synchronized (updateUserThread) {
            try {
                updateUserThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread replyThread = new ReplyThread("declined");
        replyThread.start();
        synchronized (replyThread) {
            try {
                replyThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // If it is a counter trade, go directly back to Notifications
        // Else, show a dialog
        if (counterTrade) {
            onBackPressed();
        } else {
            // Create a dialog asking if the owner wants to make a counter trade
            counterBuilder = new AlertDialog.Builder(this).create();
            counterBuilder.setMessage("Do you want to offer a counter trade?");
            counterBuilder.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    // Calls another intent
                    Intent intent = new Intent(context, CounterTradeActivity.class);
                    intent.putExtra("item_for_trade", trade.getOwnerItem());
                    intent.putExtra("borrower_name", trade.getBorrower());
                    int result = 0;

                    startActivityForResult(intent, result);
                    onBackPressed();

                }
            });
            counterBuilder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    onBackPressed();

                }
            });
            counterBuilder.show();
        }
    }

    /**
     *  Called when the owner presses the "Complete Trade" button.
     *  <p>This method is to set the trade to complete for both the owner and borrower
     *  once the items have been traded and the owner chooses to set the trade to complete.
     *
     * @param view "Complete Trade" button
     */
    public void complete(View view){
        trade.setStatus("completed");

        Thread replyThread = new ReplyThread("completed");
        replyThread.start();
        synchronized (replyThread) {
            try {
                replyThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread updateUserThread = userController.new UpdateUserThread(LoginActivity.USERLOGIN);
        updateUserThread.start();
        synchronized (updateUserThread) {
            try {
                updateUserThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        onBackPressed();
    }

    //

    /**
     * Decodes the encoded string into an image and returns it.
     * Code taken from:
     * http://stackoverflow.com/questions/4837110/how-to-convert-a-base64-string-into-a-bitmap-image-to-show-it-in-a-imageview
     * (C) 2011 user432209
     *
     * @param encoded       encoded image in string format
     * @return decodedByte  decoded image in Bitmap format
     */
    public Bitmap decodeImage(String encoded) {
        byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    /**
     * Changes the status of the trade for another user.
     */
    class ReplyThread extends Thread {
        private String status;

        public ReplyThread(String status) {
            this.status = status;
        }

        @Override
        public void run() {
            synchronized (this) {
                anotherUser = userController.getUser(anotherUsername);
                Trade tradeFound = anotherUser.getTrades().findTradeById(trade.getId());
                if (tradeFound != null) {
                    tradeFound.setStatus(status);
                    // notify the user
                    Thread updateTradeThread = userController.new UpdateUserThread(anotherUser);
                    updateTradeThread.start();
                }
                notify();
            }
        }
    }

    /**
     * E-mails both the owner and borrower with trade details and the owner's comments.
     */
    public class EmailThread extends Thread {
        private String email1;
        private String email2;
        private String comments;


        public EmailThread(String comments, String email1, String email2) {
            this.comments = comments;
            this.email1 = email1;
            this.email2 = email2;
        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    // Code taken from: http://stackoverflow.com/questions/2020088/sending-email
                    // -in-android-using-javamail-api-without-using-the-default-built-in-a
                    // (C) 2010 Vinayak B, shridutt kothari
                    GMailSender sender = new GMailSender("tradiogc@gmail.com", "tradiogc123");

                    sender.sendMail("TradioGC: New Accepted Trade",
                            comments,
                            "tradiogc@gmail.com",
                             email1+ ","+email2);


                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
                notify();
            }
        }
    }
}
