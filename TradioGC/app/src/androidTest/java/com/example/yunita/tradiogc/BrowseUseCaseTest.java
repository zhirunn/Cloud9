package com.example.yunita.tradiogc;

import android.test.ActivityInstrumentationTestCase2;

public class BrowseUseCaseTest extends ActivityInstrumentationTestCase2 {

    public BrowseUseCaseTest() {
        super(com.example.yunita.tradiogc.MainActivity.class);
    }

    public void testSearchFriendInventory(){
        Inventory inventory = new Inventory();
        Item item = new Item(1, "GAP Gift Card", "A", "Apparel", "Unused", 1, 1);
        inventory.addItem(item);

        User john = new Borrower("john");
        User anne = new Owner("anne");
        anne.addInventory(inventory);

        assertNotNull(john.searchFriendInventory(anne));

    }

    public void testBrowseInventoryByCategory(){
        Inventory inventory = new Inventory();
        Item item = new Item(1, "GAP Gift Card", "A", "Apparel", "Unused", 1, 1);
        inventory.addItem(item);

        User john = new Borrower("john");
        User anne = new Owner("anne");
        anne.addInventory(inventory);

        assertNotNull(john.browseFriendInventorybyCategory(anne, "Apparel"));
    }

    public void testBrowseInventoryByTextQuery(){
        Inventory inventory = new Inventory();
        Item item = new Item(1, "GAP Gift Card", "A", "Apparel", "Unused", 1, 1);
        inventory.addItem(item);

        User john = new Borrower("john");
        User anne = new Owner("anne");
        anne.addInventory(inventory);

        assertNotNull(john.browseFriendInventorybyTextQuery(anne, "GAP Gift Card"));
    }

}
