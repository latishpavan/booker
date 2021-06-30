package com.latish.cowinbooker.models.telegram;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Updates {
    private boolean ok;
    private List<Update> result;
}
