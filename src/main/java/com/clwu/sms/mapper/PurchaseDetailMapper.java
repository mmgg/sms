package com.clwu.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.vo.StockSummaryVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PurchaseDetailMapper extends BaseMapper<PurchaseDetail> {

    @Insert({"<script>",
            "insert into `t_parchase_detail` (batch, physic, buying_price, status) values " +
            "<foreach collection='parchaseDetails' item='item' separator=','>" +
                "(#{item.batch}, #{item.physic}, #{item.buyingPrice}, #{item.status})" +
            "</foreach>",
            "</script>"})
    void insertBatch(@Param("parchaseDetails") List<PurchaseDetail> parchaseDetailList);

    @Select("select count(1) from t_parchase_detail where physic = #{physic} and status = #{status} for update")
    int selectCountForLock(@Param("physic") Long pid, @Param("status") int status);

    @Select("SELECT " +
            "  p.id AS physicId, " +
            "  p.name AS physicName, " +
            "  p.alias AS physicAlias, " +
            "  p.manufacturer, " +
            "  p.unit, " +
            "  p.type, " +
            "  COUNT(CASE WHEN pd.status = 1 THEN 1 END) AS availableQty, " +
            "  COUNT(CASE WHEN pd.status = 2 THEN 1 END) AS occupiedQty, " +
            "  COALESCE(AVG(CASE WHEN pd.status = 1 THEN pd.buying_price END), 0) AS avgBuyingPrice " +
            "FROM t_physic p " +
            "LEFT JOIN t_parchase_detail pd ON p.id = pd.physic " +
            "GROUP BY p.id, p.name, p.alias, p.manufacturer, p.unit " +
            "ORDER BY p.name")
    @Results(id = "stockSummary", value = {
            @Result(property = "physicId", column = "physicId"),
            @Result(property = "physicName", column = "physicName"),
            @Result(property = "physicAlias", column = "physicAlias"),
            @Result(property = "manufacturer", column = "manufacturer"),
            @Result(property = "type", column = "type", javaType = Integer.class),
            @Result(property = "unit", column = "unit", javaType = Integer.class),
            @Result(property = "availableQty", column = "availableQty"),
            @Result(property = "occupiedQty", column = "occupiedQty"),
            @Result(property = "avgBuyingPrice", column = "avgBuyingPrice")
    })
    List<StockSummaryVo> selectStockSummary();
}
