package com.shequcun.farm.db;

import com.shequcun.farm.data.RecommendEntry;

/**
 * Created by apple check_turn_on 15/7/21.
 */
public class RecommendItemKey extends ItemKey {
    public Object object;

    @Override
    public void generateKeyId() {
        if (object == null)
            return;
        RecommendEntry zoneEntry = (RecommendEntry) object;
        id = createMD5(String.valueOf(zoneEntry.id));
    }
}
