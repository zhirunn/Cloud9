package com.example.yunita.tradiogc.inventory;

import java.io.Serializable;


public class Item implements Serializable{
    private String name;
    private int category;
    private double price;
    private String desc;
    private Boolean visibility;

    public Item() {

    }

    public Item(String name, int category, double price, String desc, Boolean visibility) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return this.name + ": " + this.price;
    }
}
