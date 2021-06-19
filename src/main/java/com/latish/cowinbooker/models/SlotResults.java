package com.latish.cowinbooker.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SlotResults {
    private List<SlotResult> centers;
}
