package com.clwu.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clwu.sms.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    @Select("SELECT COUNT(1) FROM t_perscription WHERE create_time >= #{startTime}")
    long selectMonthCount(@Param("startTime") LocalDateTime startTime);
}
