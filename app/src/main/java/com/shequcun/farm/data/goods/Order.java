package com.shequcun.farm.data.goods;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.shequcun.farm.data.DishesItemEntry;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private static final String TAG = "Order";
    @SerializedName("items")
    private List<DishesItemEntry> items = new ArrayList<DishesItemEntry>();
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("descr")
    private String descr;

    public DishesItemEntry getItemById(int id) {

        for (DishesItemEntry it : items) {
            if (id==(it.id)) {
                return it;
            }
        }
        return null;
    }

    public String getItemsString() {
        String result = "";
        int i = 0;
        for (DishesItemEntry item : items) {
            i++;
            if (i == items.size()) {
                result += item.id + ":" + item.getCount();
                break;
            }
            result += item.id + ":" + item.getCount() + ",";
        }
        return result;
    }

    public boolean removeItemById(int id) {

        Log.e(TAG, "id:" + id);
        for (DishesItemEntry it : items) {
            Log.e(TAG, "it's id:" + it.id);
            if (id == it.id) {
                items.remove(it);
                return true;
            }
        }
        return false;
    }

    public void clear() {
        items.clear();
    }

    public int getItemsCount() {
        int numItem = 0;
        for (DishesItemEntry it : items) {
            if (it.getCount() > 0) {
                numItem += it.getCount();
            }
        }
        return numItem;
    }

    public void addItem(DishesItemEntry item) {
        if (items == null) {
            items = new ArrayList<DishesItemEntry>();
        }
        items.add(item);
    }

    public float getItemsTotalPrice() {
        float totalPrice = 0.0f;
        int i = 0;
        for (DishesItemEntry it : items) {
            i++;
            Log.e(TAG, it.toString());
//			totalPrice = totalPrice+it.getTotalPrice();
            Log.e(TAG, "totalPrice:" + i + "/" + totalPrice);
        }
        return totalPrice;
    }

    public List<DishesItemEntry> getItems() {
        return items;
    }

    public void setItems(List<DishesItemEntry> items) {
        this.items = items;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public String toString() {
        return "Order [items=" + items + ", mobile=" + mobile + ", descr="
                + descr + "]";
    }
}
