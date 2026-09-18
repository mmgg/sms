package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 进货批次展示对象，避免前端直接展示进货人 ID。
 */
@Data
@Builder
public class PurchaseBatchVo {

    private Long id;
    private Long user;
    private String userName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
}
