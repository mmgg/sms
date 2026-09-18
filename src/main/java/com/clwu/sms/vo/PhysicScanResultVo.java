package com.clwu.sms.vo;

import com.clwu.sms.entity.Physic;
import lombok.Builder;
import lombok.Data;

/**
 * 扫码结果：本地命中时返回 physic，未命中时返回 external 供用户确认。
 */
@Data
@Builder
public class PhysicScanResultVo {

    private String source;
    private Physic physic;
    private PhysicBarcodeInfoVo external;
}
