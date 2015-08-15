package com.shequcun.farm.data.goods;

import com.google.gson.annotations.Expose;
import com.shequcun.farm.data.DishesItemEntry;

public class Item extends DishesItemEntry {

    @Expose
    private int count;


    public Item(int id, int count) {
        super();
        this.id = id;
        this.count = count;
    }

    public Item() {
        super();
    }

    public Item(int id, int count, float price) {
        super();
        this.id = id;
        this.count = count;
    }

//    public float getTotalPrice() {
//        return count * price;
////    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

//    public float getPrice() {
//        return price;
//    }

//    public void setPrice(float price) {
//        this.price = price;
//    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
