package com.example.yunita.tradiogc.inventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yunita.tradiogc.R;
import com.example.yunita.tradiogc.login.LoginActivity;
import com.example.yunita.tradiogc.trade.TradeActivity;
import com.example.yunita.tradiogc.user.UserController;

public class ItemActivity extends AppCompatActivity {
    private InventoryController inventoryController;
    private Item item;
    private Context context = this;
    private Categories categories;
    private String perspective = "";
    private UserController userController;
    private int index;

    private LinearLayout friend_panel;  // Shown when wanting to make a trade with an item
    private ImageButton edit_button;    // Shown when the item is part of the user's inventory
    private TextView name;
    private TextView category;
    private TextView price;
    private TextView description;
    private TextView quantity;
    private TextView quality;
    private ImageView itemImage;

    public void setItem(Item item) {
        this.item = item;
    }

    public TextView getName() {
        return name;
    }

    public TextView getCategory() {
        return category;
    }

    public TextView getPrice() {
        return price;
    }

    public TextView getDescription() {
        return description;
    }

    public TextView getQuantity() {
        return quantity;
    }

    public TextView getQuality() {
        return quality;
    }

    public ImageButton getEdit_button() {
        return edit_button;
    }

    public Runnable getDoUpdateGUIDetails() {
        return doUpdateGUIDetails;
    }

    private Runnable doUpdateGUIDetails = new Runnable() {
        public void run() {
            // Hasn't been tested yet
            // Need to check if the item has a photo
            // If no photo, we need to set the visibility of itemImage to "gone"
            //photo.setImage... waiting for photo to be implemented
            if (item != null) {
                name.setText(item.getName());
                category.setText(categories.getCategories().get(item.getCategory()));
                price.setText("$" + Double.toString(item.getPrice()));
                description.setText(item.getDesc());
                quantity.setText(Integer.toString(item.getQuantity()));
                if (item.getQuality() == 0) {
                    quality.setText("New");
                } else {
                    quality.setText("Used");
                }
                if (!item.getPhotos().equals("")) {
                    itemImage.setImageBitmap(decodeImage(item.getPhotos()));
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        inventoryController = new InventoryController(context);
        friend_panel = (LinearLayout) findViewById(R.id.friend_button_panel_item);
        edit_button = (ImageButton) findViewById(R.id.edit_button);
        userController = new UserController(context);



        itemImage = (ImageView) findViewById(R.id.itemImage);
        name = (TextView) findViewById(R.id.itemName);
        category = (TextView) findViewById(R.id.itemCategory);
        price = (TextView) findViewById(R.id.itemPrice);
        description = (TextView) findViewById(R.id.itemDescription);
        quantity = (TextView) findViewById(R.id.itemQuantity);
        quality = (TextView) findViewById(R.id.itemQuality);
    }

    /**
     * Alters the layout depending on if this user is viewing the item as a friend or
     * as the owner of the item.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        categories = new Categories();


        if (intent.getExtras() != null) {
            perspective = intent.getExtras().getString("owner");
            index = intent.getExtras().getInt("index");
            if (perspective.equals("owner")) {
                item = LoginActivity.USERLOGIN.getInventory().get(index);
            } else {
                item = (Item) intent.getSerializableExtra("item");
            }
            // Checks to see if we are getting a username from the intent
            if (perspective.equals("friend")) {
                edit_button.setVisibility(View.GONE);
                friend_panel.setVisibility(View.VISIBLE);
            }
        }

        runOnUiThread(doUpdateGUIDetails);
    }

    /**
     * Updates this user's inventory and the list view.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (perspective!=null && perspective.equals("owner")) {
            Thread getUserLoginThread = userController.new GetUserLoginThread(LoginActivity.USERLOGIN.getUsername());
            getUserLoginThread.start();
            synchronized (getUserLoginThread) {
                try {
                    getUserLoginThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(doUpdateGUIDetails);
            }
        }
    }

    /**
     * Activity finishes automatically if user offers a trade for this item
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            finish();
        }
    }

    /**
     * Called when the user presses the "Pencil" icon in the Item Detail page.
     * <p>This method is used to send the user to the Edit Item page.
     * It passes the item to be edited.
     *
     * @param view "Pencil" icon in Item Detail page.
     */
    public void editItem(View view) {
        Intent intent = new Intent(context, EditItemActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    // taken from http://stackoverflow.com/questions/4837110/how-to-convert-a-base64-string-into-a-bitmap-image-to-show-it-in-a-imageview
    // (C) 2011 user432209

    /**
     * Decodes the encoded string into an image and returns it.
     *
     * @param encoded encoded image in string format.
     * @return Bitmap.
     */
    public Bitmap decodeImage(String encoded) {
        byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    /**
     * Called when the user presses the "Clone" button in the Item Detail page.
     * <p>This method is used to clone the friend's item into the user's inventory.
     *
     * @param view "Clone" button
     */
    public void cloneItem(View view) {
        String name = item.getName();
        int category = item.getCategory();
        double price = item.getPrice();
        String description = item.getDesc();
        Boolean visibility = item.getVisibility();
        int quantity = item.getQuantity();
        int quality = item.getQuality();
        String photo = item.getPhotos();

        Item newItem = new Item(name, category, price, description, visibility, quantity, quality, photo);
        inventoryController.addItem(newItem);

        finish();
    }

    /**
     * Called when the user presses the "Create Trade" button in the Item Detail page.
     * <p>This method is used to send the user to the Trade page.
     *
     * @param view "Create Trade" button
     */
    public void makeTrade(View view) {
        Intent searchItemIntent = getIntent();
        String ownerName = searchItemIntent.getExtras().getString("owner_name");
        Item itemForTrade = (Item) searchItemIntent.getExtras().getSerializable("item");

        // call another intent
        Intent intent = new Intent(context, TradeActivity.class);
        intent.putExtra("owner_name", ownerName);
        intent.putExtra("item_for_trade", itemForTrade);
        int result = 0;
        startActivityForResult(intent, result);
    }
}
