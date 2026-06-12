package com.clwu.sms.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrescriptionVO {
    private Long id;
    private Long patient;
    private String patientName;
    private Long user;
    private String userName;
    private int status;
    private String comments;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long updateUser;

    public PrescriptionVO(Long id, Long patient, String patientName, Long user, String userName,
                          int status, String comments, LocalDateTime createTime,
                          LocalDateTime updateTime, Long updateUser) {
        this.id = id;
        this.patient = patient;
        this.patientName = patientName;
        this.user = user;
        this.userName = userName;
        this.status = status;
        this.comments = comments;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }
}
