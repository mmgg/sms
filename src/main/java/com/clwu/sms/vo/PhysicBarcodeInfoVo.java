package com.clwu.sms.vo;

import lombok.Data;

/**
 * 外部条码接口返回的药品专业资料。
 */
@Data
public class PhysicBarcodeInfoVo {

    private String barcode;
    private String name;
    private String spec;
    private String trademark;
    private String manufacturer;
    private String manufacturerAddress;
    private String approvalNumber;
    private String dosage;
    private String indications;
    private String mainIngredients;
    private String contraindications;
    private String precautions;
    private String storageCondition;
    private String validityPeriod;
    private String characteristics;
    private String otherNotes;
    private String note;
    private String imageUrl;
    private Integer otcType;
    private String sourceApi;
    private String sourcePayload;
}
