package com.latish.cowinbooker.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VaccinationSession {
    private String date;
    private int min_age_limit;
    private String vaccine;
    private int available_capacity;
    private List<String> slots;
    private int available_capacity_dose1;
    private int available_capacity_dose2;
}
