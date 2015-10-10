package com.shequcun.farm.compare;

import com.shequcun.farm.data.AddressEntry;

import java.util.Comparator;

/**
 * Created by cong on 15/10/10.
 */
public class AddressComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        AddressEntry entry = (AddressEntry) lhs;
        AddressEntry entry1 = (AddressEntry) rhs;
        return entry.address.compareTo(entry1.address);
    }
}
