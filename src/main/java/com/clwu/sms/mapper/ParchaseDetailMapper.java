package com.clwu.sms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clwu.sms.entity.ParchaseDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 15:53
 * @Description:
 **/
@Mapper
public interface ParchaseDetailMapper extends BaseMapper<ParchaseDetail> {
    @Insert({"<script>",
            "insert into `t_parchase_detail` (batch, physic, buying_price, status) values " +
            "<foreach collection='parchaseDetails' item='item' separator=','>" +
                "(#{item.batch}, #{item.physic}, #{item.buyingPrice}, #{item.status})" +
            "</foreach>",
            "</script>"})
    public void insertBatch(@Param("parchaseDetails") List<ParchaseDetail> parchaseDetailList);

    @Select("select count(1) from t_parchase_detail where physic = #{physic} and status = #{status} for update")
    public int selectCountForLock(@Param("physic") Long pid, @Param("status") int status);

}
