package com.latish.cowinbooker.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SlotResult {
    private int center_id;
    private String name;
    private String address;
    private int pincode;
    private String block_name;
    private List<VaccinationSession> sessions;
}
