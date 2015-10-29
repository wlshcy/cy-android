package com.shequcun.farm.data.goods;

import com.google.gson.annotations.SerializedName;
import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.data.FixedComboEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Order {
    @SerializedName("items")
    private List<DishesItemEntry> items = new ArrayList<DishesItemEntry>();
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("descr")
    private String descr;


    private List<FixedComboEntry> fItems = new ArrayList<FixedComboEntry>();
    /**
     * 备选菜容量
     */
    private List<DishesItemEntry> optionItems = new ArrayList<DishesItemEntry>();

    public DishesItemEntry getItemById(int id) {
        for (DishesItemEntry it : items) {
            if (id == it.id) {
                return it;
            }
        }
        return null;
    }

//    public DishesItemEntry getFixedItemById(int id) {
//        for (DishesItemEntry it : fixedItems) {
//            if (id == it.id) {
//                return it;
//            }
//        }
//        return null;
//    }

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

        clearOptionItems();
        items.clear();

        if (fItems != null)
            fItems.clear();
        fItems = null;
    }

    public int getItemsCount() {
        return items == null ? 0 : items.size();
    }

    public int getItemsWeight() {
        int weight = 0;
        for (DishesItemEntry it : items) {
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

//    public void addFixedItem(DishesItemEntry item) {
//        fixedItems.add(item);
//    }

    public void addOptionItem(DishesItemEntry item) {
        if (optionItems == null) {
            optionItems = new ArrayList<DishesItemEntry>();
        }
        if (!optionItems.contains(item))
            optionItems.add(item);
    }

    public void removeOptionItem(DishesItemEntry item) {
        if (optionItems == null || optionItems.size() <= 0) {
            return;
        }
        if (optionItems.contains(item))
            optionItems.remove(item);
    }


    public String getOptionItemsString() {
        String result = "";
        for (int i = 0; i < optionItems.size(); ++i) {
            if (result.length() > 0)
                result += ",";
            result += optionItems.get(i).id; //+ ":" + 1;
        }
        return result;
    }

    public List<DishesItemEntry> getOptionItems() {
        return optionItems;
    }

    public String getComboMatchItemString() {
        String result = "";
        int i = 0;
        List<FixedComboEntry> aList = getComboMatchItems();
        for (FixedComboEntry item : aList) {
            i++;
            if (i == aList.size()) {
                result += item.id + ":" + item.count;
                break;
            }
            result += item.id + ":" + item.count + ",";
        }
        return result;
    }

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
                DishesItemEntry sItemEntry = hasChoosenItem.get(j);
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
//        return buildRandomNoChooseItem(aList);
        return aList;
    }


    public void clearOptionItems() {
        if (optionItems != null)
            optionItems.clear();
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


    public void addComboMatchItem(FixedComboEntry fEntry) {
        if (fItems == null)
            fItems = new ArrayList<FixedComboEntry>();
        fItems.add(fEntry);
    }

    public void removeComboMatchItem(FixedComboEntry fEntry) {
        if (fItems != null && fItems.contains(fEntry))
            fItems.remove(fEntry);
    }


    public List<FixedComboEntry> getComboMatchItems() {
        return fItems;
    }

//    public boolean containFixedItem(Object o) {
//        return fixedItems.contains(o);
//    }
//
//    public boolean removeFixedItem(Object o) {
//        return fixedItems.remove(o);
//    }
//
//    public Set<DishesItemEntry> getFixedItems() {
//        return fixedItems;
//    }
}
