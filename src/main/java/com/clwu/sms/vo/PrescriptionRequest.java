package com.clwu.sms.vo;

import lombok.Data;
import java.util.List;

@Data
public class PrescriptionRequest {
    private Long patient;
    private Long user;
    private String comments;
    private List<Item> items;

    @Data
    public static class Item {
        private Long physic;
        private Integer num;
        private Long selling;
        private String remarks;
    }
}
