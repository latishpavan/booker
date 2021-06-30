package com.latish.cowinbooker.models.telegram;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Update {
    private int update_id;
    private Message message;
}
