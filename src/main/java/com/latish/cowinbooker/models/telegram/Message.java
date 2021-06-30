package com.latish.cowinbooker.models.telegram;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
    private Integer date;
    private String text;
}
