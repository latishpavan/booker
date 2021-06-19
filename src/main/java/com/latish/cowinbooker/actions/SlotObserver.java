package com.latish.cowinbooker.actions;

import com.latish.cowinbooker.models.SlotResult;
import com.latish.cowinbooker.models.SlotResults;

import java.util.List;

@FunctionalInterface
public interface SlotObserver {
    void onSlotAvailable(List<SlotResult> results);
}
