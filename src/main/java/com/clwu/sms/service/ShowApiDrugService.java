package com.clwu.sms.service;

import com.clwu.sms.vo.PhysicBarcodeInfoVo;

/**
 * ShowAPI 66-24 药品条码查询服务。
 */
public interface ShowApiDrugService {

    PhysicBarcodeInfoVo queryByBarcode(String barcode);
}
