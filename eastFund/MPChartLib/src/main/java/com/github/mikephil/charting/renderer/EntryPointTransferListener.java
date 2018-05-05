package com.github.mikephil.charting.renderer;

import android.graphics.PointF;

import com.github.mikephil.charting.data.Entry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 55 on 2018/1/3.
 */

public abstract class EntryPointTransferListener implements DataRenderer.OnTransferEntryToRealPointListener {
    public Map<Entry, PointF> mappingOfEntry = new HashMap<>();

    @Override
    public void onTransferToPoint(Entry entry, PointF pointF) {
        if (mappingOfEntry == null)
            return;
        if (!mappingOfEntry.containsKey(entry)) {
//            Log.d("TransEntryToRealPonit", "onTransferToPoint >>>  key: " + entry.toString() + "  value: " + pointF.toString() + "  size: " + mappingOfEntry.size());
            mappingOfEntry.put(entry, pointF);
        }
    }

    @Override
    public void onTransferFinished() {
        onTransferFinish(mappingOfEntry);
    }

    public abstract void onTransferFinish(Map<Entry, PointF> entryPointFMap);
}
