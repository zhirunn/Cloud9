package com.example.yunita.tradiogc.inventory;

import java.io.Serializable;
import java.util.Random;

/**
 * This class defines an item.
 */
public class Item implements Serializable {
    private String name;
    private int category;
    private double price;
    private String desc;
    private Boolean visibility;
    private int quantity;
    private int quality;
    private int id;

    /**
     * Class constructor for an item.
     */
    public Item() {
        name = "";
        category = 0;
        price = 0;
        desc = "";
        visibility = true;
        quantity = 1;
        quality = 0;
        Random random = new Random();
        id = random.nextInt(999999999);
    }

    /**
     * Class constructor specifying the details of the object.
     *
     * @param id         contains item's id
     * @param name       contains the name of the item
     * @param category   contains an integer that represents an index of the Categories array
     * @param price      contains a double for the price of the item
     * @param desc       contains the description/comment of the item
     * @param visibility determines whether the item is shared publicly or privately
     * @param quantity   contains an integer 1..* for the amount that the owner has of the item
     * @param quality    contains an integer that describes the item's quality; 0 is new and 1 is used
     */
    public Item(int id, String name, int category, double price, String desc, Boolean visibility, int quantity, int quality) {
        this.id = id;

        this.category = category;
        this.name = name;

        double newPrice = price * 100;
        int rounded = (int) newPrice;
        newPrice = (double) rounded;
        price = (newPrice / 100);

        this.price = price;
        this.price = price;
        this.desc = desc;
        this.visibility = visibility;
        this.quality = quality;
        this.quantity = quantity;
    }

    /**
     * Gets the id of this item.
     *
     * @return id id of the item
     */
    public int getId() {
        return id;
    }

    /**
     * Changes the id of this item.
     *
     * @param id item's new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of this item.
     *
     * @return name name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of this item.
     *
     * @param name item's new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the category of this item.
     * Category is specified by an integer from 0 - 9.
     *
     * @return category category of the item
     */
    public int getCategory() {
        return category;
    }

    /**
     * Changes the category of this item.
     * Category is specified by an integer from 0 - 9.
     *
     * @param category item's new category
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * Gets the price of this item.
     *
     * @return price price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * Changes the price of this item.
     *
     * @param price item's new price
     */
    public void setPrice(double price) {
        double newPrice = price * 100;
        int rounded = (int) newPrice;
        newPrice = (double) rounded;
        price = (newPrice / 100);
        this.price = price;
    }

    /**
     * Gets the description of this item.
     *
     * @return desc description of the item
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Changes the description of this item.
     *
     * @param desc this item's new description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets the visibility of this item.
     *
     * @return visibility visibility of the item
     */
    public Boolean getVisibility() {
        return visibility;
    }

    /**
     * Changes the visibility of this item.
     *
     * @param visibility item's new visibility
     */
    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Gets the quantity of this item.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Changes the quantity of this item.
     *
     * @param quantity item's new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the quality of this item.
     * Quality is specified by an integer: 0 (new) or 1 (used).
     *
     * @return quality quality of the item
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Changes the quality of this item.
     * Quality is specified by an integer: 0 (new) or 1 (used).
     *
     * @param quality item's new quality
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * Return the new printing format of the item.
     * <p>The new format of item is [name]\n[price].
     *
     * @return string consisting of the item's name with its price and quantity in a new line
     */
    @Override
    public String toString() {
        return this.name + "\n$" + this.price + " x " + this.quantity;
    }

}
