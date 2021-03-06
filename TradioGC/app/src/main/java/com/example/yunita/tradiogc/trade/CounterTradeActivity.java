package com.example.yunita.tradiogc.trade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yunita.tradiogc.R;
import com.example.yunita.tradiogc.inventory.Inventory;
import com.example.yunita.tradiogc.inventory.Item;
import com.example.yunita.tradiogc.login.LoginActivity;
import com.example.yunita.tradiogc.user.User;
import com.example.yunita.tradiogc.user.UserController;

/**
 * This activity handles counter trades made by the owner to the borrower.
 */
public class CounterTradeActivity extends AppCompatActivity {
    private Context context = this;

    private TextView tradeWith;
    private TextView ownerItemName;
    private TextView ownerItemPrice;
    private TextView ownerItemDescription;
    private ImageView ownerItemPhoto;

    private ListView borrowerInventoryListView;
    private ArrayAdapter<Item> borrowerInventoryArrayAdapter;
    private Inventory borrowerInventory = new Inventory();

    private ListView borrowerOfferListView;
    private ArrayAdapter<Item> borrowerOfferArrayAdapter;
    private Inventory borrowerOffer = new Inventory();

    private Item ownerItem;
    private String borrowerName;
    private User borrower;
    private Trade offeredTrade;
    private Trade pendingTrade;

    private UserController userController;

    /**
     * Gets the "Offer Trade" button for creating a counter trade.
     *
     * @return Button "Offer Trade" button.
     */
    public Button getCounterTradeButton(){
        return (Button) findViewById(R.id.offer_trade_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_display);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        borrowerInventoryListView = (ListView) findViewById(R.id.trade_inventory_list_view);
        borrowerOfferListView = (ListView) findViewById(R.id.my_offer_list_view);
        tradeWith = (TextView) findViewById(R.id.trade_with);
        ownerItemName = (TextView) findViewById(R.id.ownerItemName);
        ownerItemPrice = (TextView) findViewById(R.id.ownerItemPrice);
        ownerItemDescription = (TextView) findViewById(R.id.ownerItemDescription);
        ownerItemPhoto = (ImageView) findViewById(R.id.ownerItemPhoto);

        userController = new UserController(context);
    }

    /**
     * Initialises the trade with the details from the declined trade.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Gets the borrower name and trade item from intent
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ownerItem = (Item) bundle.getSerializable("item_for_trade");
                borrowerName = bundle.getString("borrower_name");
            }
        }

        // Waits for getting the borrower
        Thread getBorrowerThread = new GetBorrowerThread(borrowerName);
        getBorrowerThread.start();
        synchronized (getBorrowerThread) {
            try {
                getBorrowerThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Sets the inventory list and offering items list
        borrowerInventory.addAll(borrower.getInventory().getPublicItems());

        borrowerInventoryArrayAdapter = new ArrayAdapter<Item>(this, R.layout.inventory_list_item, borrowerInventory);
        borrowerInventoryListView.setAdapter(borrowerInventoryArrayAdapter);

        borrowerOfferArrayAdapter = new ArrayAdapter<Item>(this, R.layout.inventory_list_item, borrowerOffer);
        borrowerOfferListView.setAdapter(borrowerOfferArrayAdapter);


        // Sets trade with
        tradeWith.setText("Trade with " + borrower.getUsername());
        tradeWith.setTypeface(null, Typeface.BOLD);

        // set item photo and information
        //LOAD PHOTO
        //Bitmap itemPhoto = decodeImage(ownerItem.getPhotos());
        //LOAD PHOTO
        //ownerItemPhoto.setImageBitmap(itemPhoto);
        ownerItemName.setText(ownerItem.getName() + "\n" +
                "Owner: " + LoginActivity.USERLOGIN.getUsername());
        ownerItemPrice.setText("$"+Double.toString(ownerItem.getPrice()) + " x " + ownerItem.getQuantity());
        ownerItemDescription.setText(ownerItem.getDesc());

        borrowerInventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = borrowerInventory.get(position);
                borrowerInventory.remove(item);
                borrowerOffer.add(item);
                borrowerInventoryArrayAdapter.notifyDataSetChanged();
                borrowerOfferArrayAdapter.notifyDataSetChanged();
            }
        });
        borrowerOfferListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = borrowerOffer.get(position);
                borrowerOffer.remove(item);
                borrowerInventory.add(item);
                borrowerInventoryArrayAdapter.notifyDataSetChanged();
                borrowerOfferArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Called when the owner presses the "Offer Trade" button.
     * <p>This method offers a counter trade to the borrower.
     *
     * @param view "Offer Trade" button.
     */
    public void offerTrade(View view){
        // Borrower has a offered a counter trade
        offeredTrade = new Trade(LoginActivity.USERLOGIN.getUsername(), borrowerName, ownerItem, borrowerOffer);
        offeredTrade.setStatus("offered");

        // Owner has a pending counter trade
        pendingTrade = new Trade(LoginActivity.USERLOGIN.getUsername(), borrowerName, ownerItem, borrowerOffer);
        pendingTrade.setId(offeredTrade.getId());
        pendingTrade.setStatus("pending");

        // Save the pending trade in owner trades
        SavePendingTradeThread savePendingTradeThread = new SavePendingTradeThread();
        savePendingTradeThread.start();
        synchronized (savePendingTradeThread) {
            try {
                savePendingTradeThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Send notification to the borrower
        sendBorrowerNotifThread sendOwnerNotifThread = new sendBorrowerNotifThread(borrowerName);
        sendOwnerNotifThread.start();

        setResult(1);

        finish();
    }

    /**
     * Called when the owner presses the "X" button for cancelling a trade.
     * <p>This method cancels the trade that is getting composed.
     *
     * @param view "X" button
     */
    public void cancelTrade(View view) {
        // finish the parent trade detail activity
        setResult(1);
        finish();
    }

    /**
     * Decodes the encoded string into an image and returns it.
     * Code taken from:
     * http://stackoverflow.com/questions/4837110/how-to-convert-a-base64-string-into-a-bitmap-image-to-show-it-in-a-imageview
     * (C) 2011 user432209
     *
     * @param encoded       encoded image in string format
     * @return decodedByte  decoded image as a Bitmap
     */
    public Bitmap decodeImage(String encoded) {
        byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    /**
     * Sends a notification to the original borrower.
     */
    class sendBorrowerNotifThread extends Thread {
        private String username;

        public sendBorrowerNotifThread(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            // Refresh borrower
            borrower = userController.getUser(username);
            borrower.getTrades().add(0, offeredTrade);
            System.out.println(offeredTrade.getClass());
            // Notify borrower
            Thread updateTradeThread = userController.new UpdateUserThread(borrower);
            updateTradeThread.start();
        }
    }

    /**
     * Saves the trade as a pending trade for the owner.
     */
    class SavePendingTradeThread extends Thread {
        public SavePendingTradeThread() {}

        @Override
        public void run() {
            synchronized (this) {
                LoginActivity.USERLOGIN.getTrades().add(0, pendingTrade);

                Thread updateTradeThread = userController.new UpdateUserThread(LoginActivity.USERLOGIN);
                updateTradeThread.start();

                synchronized (updateTradeThread) {
                    try {
                        updateTradeThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                notify();
            }
        }
    }

    /**
     * Gets the borrower from the webserver.
     */
    class GetBorrowerThread extends Thread {
        private String name;

        public GetBorrowerThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            synchronized (this) {
                borrower = userController.getUser(name);
                notify();
            }
        }
    }
}
