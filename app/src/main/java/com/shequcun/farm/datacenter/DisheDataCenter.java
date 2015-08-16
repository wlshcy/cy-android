package com.shequcun.farm.datacenter;

import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.data.goods.Order;

import java.util.List;

/**
 * 已选择菜品数据中心
 * Created by apple on 15/8/12.
 */
public class DisheDataCenter {
    public static DisheDataCenter instance;
    Order mOrder = new Order();
    private int reqWeight = 6*500;

    public static DisheDataCenter getInstance() {
        synchronized (DisheDataCenter.class) {
            if (instance == null)
                instance = new DisheDataCenter();
            return instance;
        }
    }

    public String getOrderItemsString() {
        return mOrder.getItemsString();
    }

    public List<DishesItemEntry> getItems() {
        return mOrder.getItems();
    }

    public int getItemsCount() {
        return mOrder.getItemsCount();
    }
    public int getItemsWeight() {
        return mOrder.getItemsWeight();
    }

    public boolean isEmpty() {
        return getItemsCount() <= 0;
    }

    public DishesItemEntry getItemById(int id) {

        return mOrder.getItemById(id);
    }

    public boolean removeItemById(int id) {
        return mOrder.removeItemById(id);
    }

    public void addItem(DishesItemEntry item) {
        mOrder.addItem(item);
    }

    public boolean outOfSendPrice() {
//        Log.e(TAG, "已经选上平总价格：" + mOrder.getItemsTotalPrice());
        if (mOrder.getItemsWeight() >= reqWeight) {
            return true;
        }
        return false;
    }

    public void clear() {
        if (mOrder != null)
            mOrder.clear();
//        if (orderFinish!=null){
//            orderFinish.onFinish();
//            orderFinish = null;
//        }
    }

    public int getReqWeight() {
        return reqWeight;
    }

    public float getTotalPrice() {
        return mOrder.getItemsTotalPrice();
    }

//    public void setSendPrice(float sendPrice) {
//        this.sendPrice = sendPrice;
//    }

//    public float getSendPrice() {
//        return sendPrice;
//    }

    /**
     * 释放对应的资源
     */
    public void release() {
        if (mOrder != null)
            mOrder.clear();
        mOrder = null;
        instance = null;
    }
}
