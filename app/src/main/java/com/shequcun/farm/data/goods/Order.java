package com.shequcun.farm.data.goods;

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
            if (id == it.id) {
                return it;
            }
        }
        return null;
    }

    public String getItemsString() {
        String result = "";
        int i = 0;

        List<DishesItemEntry> aList = buildItems();

        for (DishesItemEntry item : aList) {
            i++;
            if (i == aList.size()) {
                result += item.id + ":" + item.getCount();
                break;
            }
            result += item.id + ":" + item.getCount() + ",";
        }
        return result;
    }

    public void removeItemById(int id) {
        for (DishesItemEntry it : items) {
            if (id == it.id) {
                items.remove(it);
                break;
            }
        }
    }

    public void clear() {

        for (DishesItemEntry it : items) {
            it.setCount(0);
        }

        items.clear();
    }

    public int getItemsCount() {
//        int numItem = 0;
//        for (DishesItemEntry it : items) {
//            if (it.getCount() > 0) {
//                numItem += it.getCount();
//            }
//        }
//        return numItem;
        return items == null ? 0 : items.size();
    }

    public int getItemsWeight() {
        int weight = 0;
        for (DishesItemEntry it : items) {
//            if (it.getCount() > 0) {
//                weight += (it.getCount() * it.packw);
//            }
            weight += it.packw;
        }
        return weight;
    }

    /*最大份数，0 ,表示不限制 表示该菜品最多可以选择几份*/
    public int getMaxpacksById(int id) {
        DishesItemEntry entry = getItemById(id);
        if (entry == null) return 0;
        return entry.maxpacks;
    }

    /*该菜品剩余克数*/
    public int getRemainsById(int id) {
        DishesItemEntry entry = getItemById(id);
        if (entry == null) return 999999999;
        return getItemById(id).remains;
    }

    /*指定菜品的当前份数*/
    public int getCountById(int id) {
        DishesItemEntry entry = getItemById(id);
        if (entry == null) return 0;
        return entry.getCount();
    }

    public void addItem(DishesItemEntry item) {
        if (items == null) {
            items = new ArrayList<DishesItemEntry>();
        }
        items.add(item);
    }

//    public float getItemsTotalPrice() {
//        float totalPrice = 0.0f;
//        int i = 0;
//        for (DishesItemEntry it : items) {
//            i++;
//            Log.e(TAG, it.toString());
////			totalPrice = totalPrice+it.getTotalPrice();
//            Log.e(TAG, "totalPrice:" + i + "/" + totalPrice);
//        }
//        return totalPrice;
//    }

    public List<DishesItemEntry> getItems() {
        return items;
    }

    public List<DishesItemEntry> buildItems() {
        List<DishesItemEntry> aList = new ArrayList<DishesItemEntry>();
        int size = items.size();
        for (int i = 0; i < size; ++i) {
            DishesItemEntry entry = items.get(i);
            if (aList.contains(entry)) {
                continue;
            }
            aList.add(entry);
        }
        return aList;
    }

    /**
     * 从所有菜品中筛选中未选菜品
     *
     * @param allDishesItem 所有菜品
     * @return 未选菜品
     */
    public List<DishesItemEntry> buildNoChooseItems(List<DishesItemEntry> allDishesItem) {
        List<DishesItemEntry> aList = new ArrayList<DishesItemEntry>();
        List<DishesItemEntry> hasChoosenItem = buildItems();//已选菜品
        int size = allDishesItem.size();
        int size1 = hasChoosenItem.size();
        for (int i = 0; i < size; i++) {
            boolean isAdd = false;
            DishesItemEntry pItemEntry = allDishesItem.get(i);
            for (int j = 0; j < size1; ++j) {
                DishesItemEntry sItemEntry = hasChoosenItem.get(i);
                if (pItemEntry.id == sItemEntry.id) {
                    isAdd = false;
                    break;
                } else {
                    isAdd = true;
                }
            }
            if (isAdd)
                aList.add(pItemEntry);
        }
        return aList;
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
